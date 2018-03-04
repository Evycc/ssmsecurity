'use strict';
app.factory('changePasswordService', ['$http', '$q', function($http, $q){
    const PREFIX = 'http://localhost:8080';

    var EDIT_PASSWORD_URL = PREFIX + '/editPassword';
    var factory = {
        editPassword : editPassword,
    };
    return factory;

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
}])