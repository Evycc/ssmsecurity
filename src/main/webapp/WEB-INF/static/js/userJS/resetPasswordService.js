'use strict';
app.factory('resetService', ['$http', '$q', function($http, $q){
    const PREFIX = 'http://localhost:8080';

    var RESET_PASSWORD_URL = PREFIX + '/resetPassword';
    var IS_EXPIRE_UUID = PREFIX + '/isExpireUUID';
    var factory = {
        resetPassword : resetPassword,
        isExpireUUID : isExpireUUID
    };
    return factory;

    //查询uuid是否失效
    //返回true则失效
    //返回false则uuid有效,[1]为电子邮箱
    function isExpireUUID(uuid) {
        var deferred = $q.defer();	//生成异步对象
        $http.post(IS_EXPIRE_UUID, uuid)
            .then(function (response) {
                    console.log(response);
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse);
                });
        return deferred.promise;
    }

    //返回true则重置密码成功，返回false则与现密码相同
    //参数map 包含uuid, email_guid, newPass
    function resetPassword(map) {
        var deferred = $q.defer();	//生成异步对象
        $http.post(RESET_PASSWORD_URL, map)
            .then(function (response) {
                    console.log(response);
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse);
                });
        return deferred.promise;
    }
}]);