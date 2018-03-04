'use strict';
app.factory('loginService', ['$http', '$q', function($http, $q){
    const PREFIX = 'http://localhost:8080';

    var HAS_LOGIN_ERROR = PREFIX + '/hasLoginError';
    var HAS_EXIST_USERNAME_URL = PREFIX + '/hasExistUsername';
    var factory = {
        hasErrorMsg : hasErrorMsg,
        hasExistUsername : hasExistUsername,
    };

    //是否存在用户名或邮箱
    //存在返回true
    function hasExistUsername(email) {
        var deferred = $q.defer();	//生成异步对象
        console.log(email)
        $http.post(HAS_EXIST_USERNAME_URL ,email)
            .then(
                function(response){
                    console.log(response);
                    deferred.resolve(response.data);
                },
                function(errResponse){
                    console.error(errResponse);
                    deferred.reject(errResponse);
                });
        return deferred.promise;
    }

    //是否存在登陆错误信息
    //存在则返回登陆错误信息
    function hasErrorMsg() {
        var deferred = $q.defer();	//生成异步对象
        $http.post(HAS_LOGIN_ERROR)
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
    return factory;
}])