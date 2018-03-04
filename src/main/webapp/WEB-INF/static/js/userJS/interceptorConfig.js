'usr strict';
app.factory('ajaxInterceptor', [function (userMainController) {
    var factory = {
        request : request,
        response : response
    };
    return factory;

    function request(config) {
        return config;
    }

    /**
     * 拦截rsponse
     * @param response
     * @return {*}
     */
    function response(response) {
        return response;
    }
}]).config(['$httpProvider', function ($httpProvider) {
    $httpProvider.interceptors.push('ajaxInterceptor');
}]);