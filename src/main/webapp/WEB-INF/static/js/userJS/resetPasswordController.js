'use strict';
app.controller('resetController', ['$scope', 'resetService', function($scope, resetService){
    var self = this;

    //重置密码表单
    self.passwordList = {
        password : '',
        newPassword : ''
    };

    //post请求参数
    self.postList = {
        uuid : '',
        email_guid : '',
        password : '',
        browser : '',
        os : ''
    }

    //还未重置密码时显示
    self.isShowResetBox = true;

    //重置密码成功时显示
    //为true则显示
    self.isShowSuccessReset = false;

    //显示链接失效提示
    //为true则显示
    self.isShowExpireBox = false;

    //uuid对应的电子邮箱
    self.formEmail = '';

    /*START 操作页面元素*/

    //显示密码与当前密码相同错误
    self.disErrorRepeat = function () {
        var msg = angular.element('#error_repeat');
        msg.collapse('show');
    }
    //显示密码不匹配错误
    self.disErrorNoMatch = function () {
        var msg = angular.element('#error_noMatch');
        msg.collapse('show');
    }
    //显示密码为空错误
    self.disErrorEmpty = function () {
        var msg = angular.element('#error_empty');
        msg.collapse('show');
    }
    //隐藏密码与当前密码相同错误
    self.hideErrorRepeat = function () {
        var msg = angular.element('#error_repeat');
        msg.collapse('hide');
    }
    //隐藏密码不匹配错误
    self.hideErrorNoMatch = function () {
        var msg = angular.element('#error_noMatch');
        msg.collapse('hide');
    }
    //隐藏密码为空错误
    self.hideErrorEmpty = function () {
        var msg = angular.element('#error_empty');
        msg.collapse('hide');
    }

    //隐藏第一个输入框下所有错误信息
    self.hideOneErrorMsg = function () {
        self.hideErrorRepeat();
        self.hideErrorEmpty();
    }

    // 监测输入值变化，隐藏错误信息
    $scope.$watch('ctrl.passwordList.password', function(newValue, oldValue){
        self.hideOneErrorMsg();
    })
    $scope.$watch('ctrl.passwordList.newPassword', function(newValue, oldValue){
        self.hideErrorNoMatch();
    })

    /*END 操作页面元素*/

    /*START 方法函数*/

    //解析地址，获取uuid及电子邮箱
    self.submitForm = function () {
        //?uuid=xxx&email_guid=xxxx&email_link=btn_link
        var params = window.location.search;
        var uuid = params.substring(params.indexOf('uuid=')+'uuid='.length, params.indexOf('&'));
        var email_guid = params.substring(params.indexOf('email_guid=')+'email_guid='.length, params.lastIndexOf('&'));

        if (self.passwordList.password == ''){
            self.disErrorEmpty();
            return;
        }
        if (self.passwordList.password != self.passwordList.newPassword){
            self.disErrorNoMatch();
            return;
        }

        self.postList.uuid = uuid;
        self.postList.email_guid = email_guid;
        self.postList.password = self.passwordList.password;

        self.postList.browser = self.getBrowser();  //获取当前浏览器名称
        self.postList.os = self.getOs();    //获取当前使用操作系统

        self.btnLoading('change_btn');
        resetService.isExpireUUID(self.postList.uuid)
            .then(
                function (response) {
                    if (response[0] == true){
                        //uuid失效
                        self.isShowExpireBox = true;
                        self.btnOk('change_btn');
                    }else {
                        self.formEmail = response[1];
                        //提交重置密码请求
                        resetService.resetPassword(self.postList)
                            .then(
                                function (response) {
                                    console.log(response)
                                    console.log(response[0])
                                    if (response[0] == true){
                                        self.isShowSuccessReset = true;
                                        self.isShowResetBox = false;
                                    }
                                    if (response[0] == false){
                                        self.disErrorRepeat();
                                        self.isShowSuccessReset = false;
                                        self.isShowResetBox = true;
                                    }
                                    self.btnOk('change_btn');
                                },
                                function (errResponse) {
                                    console.error(errResponse);
                                    self.btnOk('change_btn');
                                }
                            )
                    }
                },
                function (errResponse) {
                    console.error(errResponse);
                }
            )
    }

    //登陆
    self.loginUser = function () {
        self.btnLoading('login_btn');
        var form = angular.element('#login_form');
        form.submit();
        self.btnOk('login_btn');
    }

    /*START loading状态按钮*/
    self.tempBtnText = '';

    self.btnLoading = function (btnId) {
        var btn = angular.element('#' + btnId);
        self.tempBtnText = btn[0].defaultValue;
        btn[0].defaultValue = 'Loading';
        btn.attr('disabled', true);
    }
    self.btnOk = function (btnId) {
        var btn = angular.element('#' + btnId);
        btn[0].defaultValue = self.tempBtnText;
        btn.attr('disabled', false);
    }
    /*END loading状态按钮*/

    //获取当前使用的浏览器
    self.getBrowser = function () {
        var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
        var isOpera = userAgent.indexOf("Opera") > -1; //判断是否Opera浏览器
        var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera; //判断是否IE浏览器
        var isEdge = userAgent.indexOf("Edge") > -1; //判断是否IE的Edge浏览器
        var isFF = userAgent.indexOf("Firefox") > -1; //判断是否Firefox浏览器
        var isSafari = userAgent.indexOf("Safari") > -1 && userAgent.indexOf("Chrome") == -1; //判断是否Safari浏览器
        var isChrome = userAgent.indexOf("Chrome") > -1 && userAgent.indexOf("Safari") > -1; //判断Chrome浏览器

        if (isIE) {return "IE";}
        if (isFF) { return "Firefox";}
        if (isOpera) { return "Opera";}
        if (isSafari) { return "Safari";}
        if (isChrome) { return "Chrome";}
        if (isEdge) { return "Edge";}
    }

    //获取当前操作系统
    self.getOs = function () {
        var sUserAgent = navigator.userAgent;
        var isWin = (navigator.platform == "Win32") || (navigator.platform == "Windows");
        var isMac = (navigator.platform == "Mac68K") || (navigator.platform == "MacPPC") || (navigator.platform == "Macintosh") || (navigator.platform == "MacIntel");
        if (isMac) return "Mac";
        var isUnix = (navigator.platform == "X11") && !isWin && !isMac;
        if (isUnix) return "Unix";
        var isLinux = (String(navigator.platform).indexOf("Linux") > -1);
        if (isLinux) return "Linux";
        if (isWin) {
            return "Windows";
        }
    }

    /*END 方法函数*/
}])