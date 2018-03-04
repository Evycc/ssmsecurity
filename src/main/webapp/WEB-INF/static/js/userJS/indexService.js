'use strict';
app.factory('indexService', ['$http', '$q', function($http, $q){
    const PREFIX = 'http://localhost:8080';

    var RESULT_SERVICE_URL = PREFIX + '/createUser';
    var HAS_EXIST_EMMAIL_URL = PREFIX + '/hasExistEmail';
    var HAS_EXIST_USERNAME_URL = PREFIX + '/hasExistUsername';
    var HAS_LOGIN_ERROR = PREFIX + '/hasLoginError';
    var HAS_LOGIN_USER = PREFIX + '/hasLoginUser';
    var factory = {
        create : create,
        hasExistEmail : hasExistEmail,
        hasExistUsername : hasExistUsername,
        hasErrorMsg : hasErrorMsg,
        hasLoginUser : hasLoginUser
    };
    return factory;

    //存在用户则用response[0]获取，反之为空字符串
    function hasLoginUser() {
        var deferred = $q.defer();	//生成异步对象
        $http.get(HAS_LOGIN_USER)
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

    //是否存在相同邮箱
    //存在返回true
    function hasExistEmail(email) {
        var deferred = $q.defer();	//生成异步对象
        console.log(email)
        $http.post(HAS_EXIST_EMMAIL_URL ,email)
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

    //注册用户
    function create(user){console.log('create')
        var deferred = $q.defer();	//生成异步对象
        $http.post(RESULT_SERVICE_URL, user)
            .then(
                function(response){
                    console.log(response);
                    deferred.resolve(response.data);
                },
                function(errResponse){
                    console.log(errResponse);
                    deferred.reject(errResponse);
                });
        return deferred.promise;
    }
}])