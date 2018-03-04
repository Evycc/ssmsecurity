'use strict';
app.controller('loginController', ['$scope', 'loginService', function($scope, loginService){
	var self = this;

    //登录url
    self.login_url = '/login_process';

    //登陆按钮，继续按钮
    //为true	登陆操作
    //为false	需验证账号是否存在
    self.loginOrOther = false;

	//是否存在邮箱错误信息
	self.errorEmail = false;

    //错误信息，密码错误
	self.errorPassword = false;

    //为true则登录错误
    self.hasErr = false;

	//登陆错误具体信息
	self.loginErrorMessage = '';

	// 登陆表单
	self.loginUser = {
		email : '',
		password : ''
	};

	//显示登陆表单密码框
	self.disLoginFormPasswordInput = function(){
		var loginFormPasswordInput = angular.element("#passwordDiv");
		loginFormPasswordInput.collapse('show');
	}
	//显示登陆表单button
	self.disLoginFormBtn = function(){
		var loginFormBtn = angular.element("#loginBtnDiv");
		loginFormBtn.collapse('show');
	}
	//显示登陆表单rememberMe
	self.disLoginRem = function(){
		var loginFormRem = angular.element("#remChk");
		loginFormRem.collapse('show');
	}
	//显示login表单密码错误信息
	self.disLoginPassErr = function(){
		var loginFormPasswordError = angular.element("#passColl");
		loginFormPasswordError.collapse('show');
	}
	// 显示login用户名错误信息
	self.disLoginUsernameErr = function(){
		var loginFormUsernameErr = angular.element('#loginUserColl');
		loginFormUsernameErr.collapse('show');
	}

	//隐藏登陆表单密码框
	self.hideLoginFormPasswordInput = function(){
		var loginFormPasswordInput = angular.element("#passwordDiv");
		loginFormPasswordInput.collapse('hide');
	}
	//隐藏登陆表单button
	self.hideLoginFormBtn = function(){
		var loginFormBtn = angular.element("#loginBtnDiv");
		loginFormBtn.collapse('hide');
	}
	//隐藏登陆表单rememberMe
	self.hideLoginRem = function(){
		var loginFormRem = angular.element("#remChk");
		loginFormRem.collapse('hide');
	}
	//隐藏login表单密码错误信息
	self.hideLoginPassErr = function(){
		var loginFormPasswordError = angular.element("#passColl");
		loginFormPasswordError.collapse('hide');
	}
	// 隐藏login用户名错误信息
	self.hideLoginUsernameErr = function(){
		var loginFormUsernameErr = angular.element('#loginUserColl');
		loginFormUsernameErr.collapse('hide');
	}

	// errorPassword = true 显示密码错误信息
	self.disLoginPasswordError = function(){
		if (self.errorPassword) {
			self.disLoginPassErr();
		}else{
			self.hideLoginPassErr();
		}
	}

	// errorEmail = true 显示邮箱错误信息
	self.disEmailError = function(){
		if (self.errorEmail) {
			//隐藏登录框
			self.hideLoginRem();
			self.hideLoginFormPasswordInput();
			// self.hideLoginFormBtn();
			//显示邮箱错误信息
			self.disLoginUsernameErr();
		}else {
			self.hideLoginUsernameErr();
		}
	}

	/*START 监测输入框数据*/
	$scope.$watch('ctrl.loginUser.password', function (newValue, oldValue) {
		if (newValue != oldValue){
            self.errorPassword = false;
            self.disLoginPasswordError();
            // self.disLoginRem();
		}
    })
    $scope.$watch('ctrl.loginUser.email', function (newValue, oldValue) {
        self.hideAllError();
        self.disLoginFormBtn();
    })

    /*END 监测输入框数据*/

	/*START 向服务器查看是否存在登录错误信息*/
	self.hasLoginErr = function () {
		loginService.hasErrorMsg()
			.then(
				function (response) {
					console.log(response);
					if (response != ''){
                        self.loginErrorMessage = response[0];
						self.hasErr = true;
					}else {
						self.hasErr = false;
					}
                    self.alertErrorMsg();
				},
				function (errResponse) {
					console.error(errResponse);
				})
    }

    //hasErr = true 显示密码错误信息
    self.alertErrorMsg = function () {
		if (self.hasErr){
            self.ifHasLoginError();
		}
    }

    //存在登录错误信息
    self.ifHasLoginError = function () {
		console.log("hasError")
		self.errorPassword = true;
        self.disLoginPasswordError();
        self.hideLoginRem();
        // self.hideLoginFormBtn();
    }
    /*END 向服务器查看是否存在登录错误信息*/

    /*START 屏蔽所有错误信息*/
    self.hideAllError = function () {
		self.errorEmail = false;
		self.errorPassword = false;
		self.disEmailError();
		self.disLoginPasswordError()
    }
    /*END 屏蔽所有错误信息*/

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

    /*START提交登陆表单*/
    self.loginFormSubmit = function(){
        if (self.loginOrOther){
            self.btnLoading('loginBtn');
            var login = angular.element('#loginForm');
            login.attr('action',self.login_url);
            login.attr('method','post');
            login.submit();
        }else {
            self.btnLoading('loginBtn');
            loginService.hasExistUsername(self.loginUser.email)
                .then(
                    function (response) {
                        console.log(response);
                        if (response == 'true'){
                            self.loginOrOther = true;
                            self.hideAllError();
                            self.disLoginFormPasswordInput();
                            self.disLoginRem();
                            self.btnOk('loginBtn');
                        }else{
                            self.loginOrOther = false;
                            self.errorUserOREmail = true;
                            self.disLoginUsernameErr();
                            self.hideLoginFormPasswordInput();
                            self.hideLoginRem();
                            self.btnOk('loginBtn');
                        }
                    },
                    function (errRespose) {
                        console.error(errRespose);
                        self.btnOk('loginBtn');
                    }
                )
        }
    }
    /*END提交登陆表单*/

    self.hasLoginErr();
    self.disLoginFormBtn();
}])