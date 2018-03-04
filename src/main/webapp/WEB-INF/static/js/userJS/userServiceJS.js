'use strict';
app.factory('userService', ['$http', '$q', function ($http, $q) {
    const PREFIX = 'http://localhost:8080';
    const USER_PREFIX = 'http://localhost:8080/user';

    //user请求
    var GET_CURRENT_USER_URL = USER_PREFIX + '/getUserAndRole';
    var GET_USER_HEADER_ID = USER_PREFIX + '/headerId';
    var GET_USER_INFO_URL = USER_PREFIX + '/getUserInfo';
    var UPDATE_USER_INFO_URL = USER_PREFIX + '/editUser';
    var GET_ALL_USER_URL = USER_PREFIX + '/getAllUser';
    var LOCKED_USER_URL = USER_PREFIX + '/lockedUser';
    var DELETE_USER_URL = USER_PREFIX + '/deleteByUserId';
    var GET_ALL_USER_PAGE_URL = USER_PREFIX + '/getAllUserPage';
    var UNLOCK_USER_URL = USER_PREFIX + '/unlockUser';
    var GET_ONLINE_NUM_URL = USER_PREFIX + '/getOnlineNum';
    var GET_ONLINE_USER_URL = USER_PREFIX + '/getOnlineUser';
    var KICKOUT_USER_URL = USER_PREFIX + '/kickoutByEmail';
    var GET_USER_ROLE_URL = USER_PREFIX + '/getRoleManage';
    var GET_ALL_ROLE_URL = USER_PREFIX + '/getAllRole';
    var INSERT_USER_ROLE_URL = USER_PREFIX + '/addRoleByUsername';
    var DEL_USER_ROLE_URL = USER_PREFIX + '/delRoleByUsername';
    var IS_ONLINE_USERNAME_URL = USER_PREFIX + '/isOnlineUsername';
    var SEND_TO_USER_URL = USER_PREFIX + '/ws/sendToUser';
    var GET_OFFLINE_MESSAGE_URL = USER_PREFIX + '/ws/getOfflineMessage';

    //普通请求
    var UPLOAD_IMG_URL = PREFIX + '/uploadFile';
    var EDIT_PASSWORD_URL = PREFIX + '/editPassword';
    var GET_SESSIION_INTERVAL = PREFIX + '/getSessionInterval';
    var SESSION_EXPIRE_TIME = PREFIX + '/sessionExpireInterval';

    

    var factory = {
        getCurrentUserAndRole: getCurrentUserAndRole,
        uploadImage: uploadImage,
        getUserHeaderId: getUserHeaderId,
        getUserInfo: getUserInfo,
        updateUserInfo: updateUserInfo,
        editPassword: editPassword,
        getAllUser: getAllUser,
        lockedUser: lockedUser,
        deleteUser: deleteUser,
        getAllUserPage: getAllUserPage,
        unlockUser: unlockUser,
        getOnlineNum: getOnlineNum,
        getOnlineUsers: getOnlineUsers,
        kickoutUser: kickoutUser,
        getUserRole: getUserRole,
        getAllRole: getAllRole,
        addUserRole: addUserRole,
        delUserRole: delUserRole,
        sendToUser: sendToUser,
        getOfflineMessage : getOfflineMessage,
        isOnlineByUsername : isOnlineByUsername,
        sessionExpireInterval : sessionExpireInterval,
        getSessionTime : getSessionTime
    };
    return factory;

    function sessionExpireInterval(){
        var deferred = $q.defer();	//生成异步对象
        $http.post(SESSION_EXPIRE_TIME)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 获取session到期间隔时间
     * @return {*|promise|f}    返回时间（单位:%s)
     */
    function getSessionTime() {
        var deferred = $q.defer();	//生成异步对象
        $http.post(GET_SESSIION_INTERVAL)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 获取from用户对应to用户的离线信息
     * @param map 存在两个key(from,to)<br>  from  接收离线信息的用户<br> to    发送离线信息的用户
     */
    function getOfflineMessage(map) {
        var deferred = $q.defer();	//生成异步对象
        $http.post(GET_OFFLINE_MESSAGE_URL, map)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 发送信息给指定用户
     * @param map   发送消息体
     * @returns {*|promise|f}
     */
    function sendToUser(map) {
        var deferred = $q.defer();	//生成异步对象
        $http.post(SEND_TO_USER_URL, map)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 查询用户是否在线
     * @param username  用户标识
     * @returns {*|promise|f}   在线返回true，反之返回false
     */
    function isOnlineByUsername(username) {
        var deferred = $q.defer();	//生成异步对象
        $http.post(IS_ONLINE_USERNAME_URL, username)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 删除用户中指定角色
     * @param map   用户邮箱及对应角色
     * @returns {*|promise|f}
     */
    function delUserRole(map) {
        var deferred = $q.defer();	//生成异步对象
        $http.post(DEL_USER_ROLE_URL, map)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 为用户新增角色
     * @param map   用户邮箱及对应角色
     * @returns {*|promise|f}
     */
    function addUserRole(map) {
        var deferred = $q.defer();	//生成异步对象
        $http.post(INSERT_USER_ROLE_URL, map)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 获取所有角色
     * @returns {*|promise|f}
     */
    function getAllRole() {
        var deferred = $q.defer();	//生成异步对象
        $http.get(GET_ALL_ROLE_URL)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 获取全部用户及角色
     * @returns {*|promise|f}   [0]:用户数组 [1]:角色数组
     */
    function getUserRole() {
        var deferred = $q.defer();	//生成异步对象
        $http.get(GET_USER_ROLE_URL)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 踢出用户
     * @param email 用户电子邮箱
     * @returns {*|promise|f}
     */
    function kickoutUser(email) {
        var deferred = $q.defer();	//生成异步对象
        $http.post(KICKOUT_USER_URL, email)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 获取在线用户列表
     * @returns {*|promise|f}
     */
    function getOnlineUsers() {
        var deferred = $q.defer();	//生成异步对象
        $http.get(GET_ONLINE_USER_URL)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 获取在线人数
     * @returns {*|promise|f}
     */
    function getOnlineNum() {
        var deferred = $q.defer();	//生成异步对象
        $http.get(GET_ONLINE_NUM_URL)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 解锁用户账号
     * @param id    用户主键
     * @returns {*|promise|f}
     */
    function unlockUser(id) {
        var deferred = $q.defer();	//生成异步对象
        $http.post(UNLOCK_USER_URL, id)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 获取分页用户信息
     * @param page  分页类
     * @returns {*|promise|f}
     */
    function getAllUserPage(page) {
        var deferred = $q.defer();	//生成异步对象
        $http.post(GET_ALL_USER_PAGE_URL, page)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 根据用户主键,删除用户账号
     * @param userId    用户主键
     * @returns {*|promise|f}
     */
    function deleteUser(userId) {
        var deferred = $q.defer();	//生成异步对象
        $http.post(DELETE_USER_URL, userId)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 根据用户主键,锁定用户账号
     * @param userId    用户主键
     * @returns {*|promise|f}
     */
    function lockedUser(userId) {
        var deferred = $q.defer();	//生成异步对象
        $http.post(LOCKED_USER_URL, userId)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 获取全部用户信息，分页的形式
     * @returns {*|promise|f}
     */
    function getAllUser() {
        var deferred = $q.defer();	//生成异步对象
        $http.get(GET_ALL_USER_URL)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 发送更改密码请求
     * @param email 用户名或电子邮箱
     * @returns {*|promise|f}
     */
    function editPassword(email) {
        var deferred = $q.defer();	//生成异步对象
        $http.post(EDIT_PASSWORD_URL, email)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 更新用户个人资料
     * @param user  用户个人资料表单
     * @returns {*|promise|f}   0 : 更新错误信息
     */
    function updateUserInfo(user) {
        var deferred = $q.defer();	//生成异步对象
        $http.post(UPDATE_USER_INFO_URL, user)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 获取用户个人资料
     * @param username  用户邮箱
     * @returns {*|promise|f}   返回 0 : 用户名，1 : 电子邮箱
     */
    function getUserInfo(username) {
        var deferred = $q.defer();	//生成异步对象
        $http.post(GET_USER_INFO_URL, username)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse);
                });
        return deferred.promise;
    }

    /**
     * 获取当前用户头像
     * @param username  用户邮箱
     * @returns {*|promise|f}   返回用户头像id
     */
    function getUserHeaderId(username) {
        var deferred = $q.defer();	//生成异步对象
        $http.post(GET_USER_HEADER_ID, username)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse);
                });
        return deferred.promise;
    }

    /**
     * 上传用户头像
     * @param fd    包含 logo 和 rect <br>
     * rect : 截取图片坐标
     * logo : 上传图片
     * @returns {*|promise|f}
     */
    function uploadImage(fd) {
        var deferred = $q.defer();	//生成异步对象
        $http.post(UPLOAD_IMG_URL, fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        })
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 获取当前登录用户信息
     * @returns {*|promise|f}   0 : 用户名 1 : 角色名
     */
    function getCurrentUserAndRole() {
        var deferred = $q.defer();	//生成异步对象
        $http.get(GET_CURRENT_USER_URL)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse);
                });
        return deferred.promise;
    }
}]);