package com.evy.jing.daoImpl;

import com.evy.jing.dao.SeUserInfoDao;
import com.evy.jing.mapper.SeUserInfoMapper;
import com.evy.jing.model.SeUserInfo;
import com.evy.jing.model.SeUserInfoExample;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class SeUserInfoDaoImpl implements SeUserInfoDao {
    @Resource
    SeUserInfoMapper seUserInfoMapper;

    @Override
    public int insert(SeUserInfo seUserInfo) {
        return seUserInfoMapper.insertSelective(seUserInfo);
    }

    @Override
    public int update(SeUserInfo seUserInfo) {
        SeUserInfoExample example = new SeUserInfoExample();
        SeUserInfoExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(seUserInfo.getId());

        return seUserInfoMapper.updateByExampleSelective(seUserInfo, example);
    }

    @Override
    public int delete(Integer id) {
        SeUserInfoExample example = new SeUserInfoExample();
        SeUserInfoExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);

        return seUserInfoMapper.deleteByExample(example);
    }

    /**
     * 根据主键查找SeUserInfo
     * @param id
     * @return  为空返回Null
     */
    @Override
    public SeUserInfo selectByPrimaryKey(Integer id) {
        SeUserInfoExample example = new SeUserInfoExample();
        SeUserInfoExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(id);

        List<SeUserInfo> list = seUserInfoMapper.selectByExample(example);

        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<SeUserInfo> findByUserId(Integer userId) {
        SeUserInfoExample example = new SeUserInfoExample();
        SeUserInfoExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);

        List<SeUserInfo> list = seUserInfoMapper.selectByExample(example);

        return list.size() > 0 ? list : null;
    }
}
