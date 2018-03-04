'use strict';
app.controller('changePasswordController', ['$scope', 'changePasswordService', function($scope, changePasswordService){
    var self = this;

    //form  用户名或邮箱
    self.inputUsername = '';

    //是否显示发送邮件成功信息
    //为true显示，反之隐藏
    self.isShowSendMailSuccess = false;

    //显示发送更改密码请求的按钮
    //为true显示，反之隐藏
    self.isShowSendForm = true;

    /*START 操作页面元素*/

    /*START loading状态按钮*/
    self.tempBtnText = '';

    self.btnLoading = function (btnId) {
        var btn = angular.element('#' + btnId);
        console.log(btn)
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

    //显示输入用户名为空错误信息
    self.disErrorEmpty = function () {
        var msg = angular.element('#error_repeat');
        msg.collapse('show');
    }
    //隐藏输入用户名为空错误信息
    self.hideErrorEmpty = function () {
        var msg = angular.element('#error_repeat');
        msg.collapse('hide');
    }

    //发送更改密码请求之前
    self.sendBefore = function () {
        self.isShowSendMailSuccess = false;
        self.isShowSendForm = true;
    }

    //发送更改密码请求后
    self.sendSuccess = function () {
        self.isShowSendMailSuccess = true;
        self.isShowSendForm = false;
    }

    //监听输入框，清除错误信息
    $scope.$watch("ctrl.inputUsername", function (oldValue, newValue) {
        self.hideErrorEmpty();
    })

    /*END 操作页面元素*/

    /*START 方法*/
    self.sendChangeEmail = function () {
        if(self.inputUsername == ''){
            self.disErrorEmpty();
            return;
        }
        self.btnLoading('send_btn');
        changePasswordService.editPassword(self.inputUsername)
            .then(
                function (response) {
                    self.btnOk('send_btn');
                    self.sendSuccess();
                },
                function (errResponse) {
                    self.btnOk('send_btn');
                    self.sendBefore();
                }
            )
    }
    /*END 方法*/
}])