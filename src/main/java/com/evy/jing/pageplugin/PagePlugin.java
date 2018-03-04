package com.evy.jing.pageplugin;

import com.evy.jing.util.LoggerUtils;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import javax.xml.bind.PropertyException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 分页插件类
 * MyBatis 3.4.1以上版本使用args={Connection.class, Integer.class}
 * MyBatis 3.4.1一下版本使用args={Connection.class}
 * 拦截方法名称为 prepare 的方法，
 * 这个方法是每次数据库访问都要执行的
 */
@Intercepts({
        @Signature(type = StatementHandler.class,
                method = "prepare",
                args = {Connection.class, Integer.class})
})
public class PagePlugin implements Interceptor {
    /**
     * 数据库方言
     */
    private static String dialect = "";
    /**
     * 过滤方法，以正则表达式的方法匹配
     */
    private static String pageSqlId = "";

    /**
     * 实现拦截逻辑的地方，
     * 内部通过Invocation.proceed()显示地推进责任链前进
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getTarget() instanceof RoutingStatementHandler) {
            RoutingStatementHandler routingStatementHandler =
                    (RoutingStatementHandler) invocation.getTarget();
            StatementHandler statementHandler = (StatementHandler) ReflectHelper
                    .getValueByFieldName(routingStatementHandler, "delegate");
            //MappedStatement   mybatis核心配置
            MappedStatement mappedStatement = (MappedStatement) ReflectHelper.getValueByFieldName(
                    statementHandler, "mappedStatement");

            if (mappedStatement.getId().matches(pageSqlId)) {
                //BoundSql  包含sql语句，参数
                BoundSql boundSql = statementHandler.getBoundSql();
                Object object = boundSql.getParameterObject();
                if (object == null) {
                    LoggerUtils.error(getClass(), new NullPointerException(), "参数为空");
                } else {
                    //prepare方法参数是一个Connection
                    Connection connection = (Connection) invocation.getArgs()[0];
                    //获取当前执行的sql语句
                    String sql = boundSql.getSql();

                    PageModel pageModel = null;
                    if (object instanceof PageModel) {
                        pageModel = (PageModel) object;
                    } else if (object instanceof Map) {
                        Map<String, Object> map = (Map<String, Object>) object;
                        pageModel = (PageModel) map.get("pageModel");
                        if (pageModel == null) {
                            pageModel = new PageModel();
                        }
                    } else {
                        Field field = ReflectHelper.getFieldByFieldName(object, "pageModel");
                        if (field != null) {
                            pageModel = (PageModel) ReflectHelper.getValueByFieldName(object, "pageModel");
                            if (pageModel == null) {
                                pageModel = new PageModel();
                            } else {
                                LoggerUtils.error(object.getClass(), new NoSuchFieldException(), "获取不到字段pageModel");
                            }
                        }
                    }

                    //设置分页总页数
                    pageModel.setTotalCount(getTotalCount(mappedStatement, connection, boundSql));
                    //获取分页sql语句
                    String pageSql = buildPageSQL(sql, pageModel);

                    //test
                    System.out.println("pageSql = " + pageSql);

                    //利用反射设置当前BoundSql对应的sql属性为我们建立好的分页Sql语句
                    ReflectHelper.setValueByFieldName(boundSql, "sql", pageSql);
                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        //当目标类是指定的拦截类时，才包装目标类，否则直接返回目标本身，
        //减少目标被代理的次数
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    /**
     * 设置额外参数，参数配置在拦截器Properties节点里
     *
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {
        dialect = properties.getProperty("dialect");
        if (dialect == null || "".equals(dialect)) {
            LoggerUtils.error(getClass(), new PropertyException("数据库方言dialect为空!"),
                    "数据库方言dialect为空");
        }
        pageSqlId = properties.getProperty("pageSqlId");
        if (pageSqlId == null || "".equals(pageSqlId)) {
            LoggerUtils.error(getClass(), new PropertyException("拦截匹配规则pageSqlId为空!"),
                    "拦截匹配规则为空");
        }
    }

    /**
     * 返回分页总页数
     *
     * @param statement
     * @param connection
     * @param boundSql
     * @return
     */
    private int getTotalCount(MappedStatement statement, Connection connection,
                              BoundSql boundSql) throws SQLException {
        int totalCount = 0;

        String sql = boundSql.getSql();
        String countSql = getCountSQL(sql);

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        preparedStatement = connection.prepareStatement(countSql);
        try {
            //设置参数
            setParam(preparedStatement, statement, boundSql, boundSql.getParameterObject());

            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                totalCount = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return totalCount;
    }

    /**
     * 添加预处理参数
     *
     * @param preparedStatement 预处理类
     * @param mappedStatement   mybatis核心配置类
     * @param boundSql          包含sql语句及参数
     * @param paramObject       boundSql中的参数对象
     */
    private void setParam(PreparedStatement preparedStatement, MappedStatement mappedStatement, BoundSql boundSql,
                          Object paramObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        //获取参数映射
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();
        if (parameterMappingList != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            //类型处理器
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            //MetaObject    可直接访问对象属性
            MetaObject metaObject = paramObject == null ? null : configuration.newMetaObject(paramObject);

            for (int i = 0; i < parameterMappingList.size(); i++) {
                ParameterMapping parameterMapping = parameterMappingList.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer tokenizer = new PropertyTokenizer(propertyName);
                    if (paramObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(paramObject.getClass())) {
                        //查询是否存在参数相关类型
                        value = paramObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        //Parame注释产生的字段
                        //查询相关Parame注释是否存在对应参数
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX) &&
                            boundSql.hasAdditionalParameter(tokenizer.getName())) {
                        value = boundSql.getAdditionalParameter(tokenizer.getName());
                        if (value == null) {
                            value = configuration.newMetaObject(value).getValue(
                                    propertyName.substring(tokenizer.getName().length()));
                        }
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    if (typeHandler != null) {
                        throw new ExecutorException("存在无法转换类型的参数 "
                                + propertyName + "of statement" + mappedStatement.getId());
                    }
                    typeHandler.setParameter(preparedStatement, i, value, parameterMapping.getJdbcType());
                }
            }
        }
    }

    /**
     * 查询总记录数的sql语句
     *
     * @param sql
     * @return
     */
    private String getCountSQL(String sql) {
        StringBuilder stringBuilder = new StringBuilder();
        int index = sql.indexOf("FROM");
        stringBuilder.append("SELECT COUNT(*)");
        stringBuilder.append(sql.substring(index));
        return stringBuilder.toString();
    }

    /**
     * 根据数据库方言获取分页语句
     *
     * @param sql
     * @param pageModel
     * @return
     */
    private String buildPageSQL(String sql, PageModel pageModel) {
        String mysqlStr = "mysql";
        String oracleStr = "oracle";

        boolean dialectIsEmpty = !"".equals(dialect) || dialect != null;
        if (pageModel != null && dialectIsEmpty) {
            StringBuilder stringBuilder = new StringBuilder();
            if (mysqlStr.equalsIgnoreCase(dialect)) {
                stringBuilder = buildPageSQLByMYSQL(sql, pageModel);
            }
            if (oracleStr.equalsIgnoreCase(dialect)) {
                stringBuilder = buildPageSqlForOracle(sql, pageModel);
            }
            return stringBuilder.toString();
        }
        return sql;
    }

    /**
     * MYSQL分页语句
     *
     * @param sql
     * @param pageModel
     * @return
     */
    private StringBuilder buildPageSQLByMYSQL(String sql, PageModel pageModel) {
        StringBuilder pageSql = new StringBuilder(100);
        pageSql.append(sql);
        pageSql.append(" LIMIT " + pageModel.getCurrentResult() + "," + pageModel.getShowCount());
        return pageSql;
    }

    /**
     * 返回oracle分页sql语句
     *
     * @param sql
     * @param pageModel
     * @return
     */
    public StringBuilder buildPageSqlForOracle(String sql, PageModel pageModel) {
        StringBuilder pageSql = new StringBuilder(100);
        //搜索结果记录为当前页数-1 * 显示页数
        String beginRow = String.valueOf((pageModel.getCurrentPage() - 1) * pageModel.getShowCount());
        String endRow = String.valueOf((pageModel.getCurrentPage()) * pageModel.getShowCount());
        pageSql.append("SELECT * FROM (SELECT tmp_tb.*,ROWNUM row_id from ()");
        pageSql.append(sql);
        pageSql.append(")  tmp_tb where ROWNUM<=").append(endRow);
        pageSql.append(") where row_id>").append(beginRow);
        pageSql.append(pageModel.getCurrentResult());
        return pageSql;
    }
}
