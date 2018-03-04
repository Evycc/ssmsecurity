'use strict';
app.controller('indexController', ['$scope', 'indexService',
	function($scope, indexService){
	var self = this;
    //登陆按钮，继续按钮
	//为true	登陆操作
	//为false	需验证账号是否存在
	self.loginOrOther = false;
    //错误信息，注册邮箱已存在
	self.errorEmailExist = false;

    //错误信息，用户名或邮箱不存在
	self.errorUserOREmail = false;
    //错误信息，密码错误
	self.errorPassword = false;

	//登陆错误具体信息
	self.loginErrorMessage = '';

	//登陆表单action
	self.loginURL = '/login_process';

	//用户是否登录
	self.isUserLogin = false;

	//当前登录用户username或email
	self.currentUser = '';

	// 注册表单
	self.registerUser = {
		email : '',
		password : ''
	};

	// 登陆表单
	self.loginUser = {
		email : '',
		password : ''
	};

	/*屏蔽vedio标签右键功能*/
	self.hideFalseVedio = function(){
		var falseVedio = angular.element("#bgVideo");
		falseVedio.bind("contextmenu",function(){
			return false;
		})
	}
	// 显示注册表单
	self.disRegisterForm = function(){
		var registerForm = angular.element("#contextOne");
		registerForm.collapse('show');
		self.hideLoginForm();
	}
	// 显示登陆表单	隐藏注册表单
	self.disLoginForm = function(){
		var loginForm = angular.element("#contextTwo");
		loginForm.collapse('show');
		self.disLoginFormBtn();
		self.hideRegisterForm();
	}
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
		var loginFOrmUsernameErr = angular.element('#loginUserColl');
		loginFOrmUsernameErr.collapse('show');
	}
	// 显示 registerForm密码为空错误信息
	self.disRegisterPassErr = function(){
		var registerPassErr = angular.element('#registerPasswordErr');
		registerPassErr.collapse('show');
	}
	// 显示 registerForm邮箱格式错误信息
	self.disRegisterEamilFormatErr = function(){
		var registerPassErr = angular.element('#registerEmailFormatErr');
		registerPassErr.collapse('show');
	}
	// 显示 registerForm邮箱已存在错误信息
	self.disRegisterEamilExistErr = function(){
		var registerPassErr = angular.element('#registerEmailExistErr');
		registerPassErr.collapse('show');
	}
	// 显示 registerForm邮箱为空错误信息
	self.disRegisterEamilRequiredErr = function(){
		var registerPassErr = angular.element('#registerEmailRequiredErr');
		registerPassErr.collapse('show');
	}

	// 隐藏注册表单
	self.hideRegisterForm = function(){
		var registerForm = angular.element("#contextOne");
		registerForm.collapse('hide');
	}
	// 隐藏登陆表单
	self.hideLoginForm = function(){
		var loginForm = angular.element("#contextTwo");
		loginForm.collapse('hide');
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
		var loginFOrmUsernameErr = angular.element('#loginUserColl');
		loginFOrmUsernameErr.collapse('hide');
	}
	// 隐藏 registerForm密码为空错误信息
	self.hideRegisterPassErr = function(){
		var registerPassErr = angular.element('#registerPasswordErr');
		registerPassErr.collapse('hide');
	}
	// 隐藏 registerForm邮箱格式错误信息
	self.hideRegisterEamilFormatErr = function(){
		var registerPassErr = angular.element('#registerEmailFormatErr');
		registerPassErr.collapse('hide');
	}
	// 隐藏 registerForm邮箱已存在错误信息
	self.hideRegisterEamilExistErr = function(){
		var registerPassErr = angular.element('#registerEmailExistErr');
		registerPassErr.collapse('hide');
	}
	// 隐藏 registerForm邮箱为空错误信息
	self.hideRegisterEamilRequiredErr = function(){
		var registerPassErr = angular.element('#registerEmailRequiredErr');
		registerPassErr.collapse('hide');
	}

	// 登陆错误时，隐藏按钮
	self.hideOnLoginError = function(){
		self.hideLoginFormBtn();
		self.hideLoginRem();
	}

	// 用户名/邮箱错误时，隐藏密码框等
	self.hideOnUsername = function(){
		self.hideLoginFormPasswordInput();
		self.hideOnLoginError();
	}

	// 监测login表单Username输入值变化，隐藏错误信息
	$scope.$watch('ctrl.loginUser.email', function(newValue, oldValue){
        self.hideAllLoginError();
	})

	// 监测login表单Password输入值变化，隐藏错误信息
	$scope.$watch('ctrl.loginUser.password', function(newValue, oldValue){
        self.hideAllLoginError();
	})

	// 监测register表单Email输入值变化，显示错误信息
	$scope.$watch('ctrl.registerUser.email', function(newValue, oldValue){
		//隐藏邮箱格式错误信息
		self.hideRegisterEamilFormatErr();

		//隐藏邮箱输入值为空信息
		self.hideRegisterEamilRequiredErr();

		//隐藏邮箱已存在错误信息
		self.errorEmailExist = false;
		self.disEmailExistError();
	})

	// 监测register表单密码Password输入值变化，隐藏错误信息
	$scope.$watch('ctrl.registerUser.password', function(newValue, oldValue){
		self.hideRegisterPassErr();
	})

	// true 显示密码错误信息
	self.disLoginPasswordError = function(){
		if (self.errorPassword) {
			self.disLoginForm();
			self.disLoginPassErr();
		}else{
			self.hideLoginPassErr();
		}
	}

	// true 显示登陆用户名不存在信息
	self.disLoginUserError = function(){
		if (self.errorUserOREmail) {
			self.hideRegisterForm();
			self.disLoginForm();
			self.disLoginUsernameErr();
		}else{
			self.hideLoginUsernameErr();
		}
	}

	// self.errorEmailExist = true 显示注册邮箱已存在信息
	self.disEmailExistError = function(){
		if (self.errorEmailExist) {
			self.disRegisterEamilExistErr();
		}else{
			self.hideRegisterEamilExistErr();
		}
	}

	//提交注册表单
	self.registerFormSubmit = function(){
		if(self.registerUser.email == '') {
			self.disRegisterEamilRequiredErr();
			return;
		}else{
			var filter = /^([a-zA-Z0-9.])+@([a-zA-Z0-9.]+\.)+([a-zA-Z0-9.]{2,4})+$/;
			if (!filter.test(self.registerUser.email)) {
				self.disRegisterEamilFormatErr();
				return;
			}
		}
		if (self.registerUser.password == '' ) {
			self.disRegisterPassErr();
			return;
		}
		self.registerLoading();
		indexService.hasExistEmail(self.registerUser.email)
			.then(
				function (response) {
					//存在的邮箱，提示错误信息
					if (response == 'true'){
                        self.errorEmailExist = true;
                        self.disEmailExistError();
                        self.registerOk();
					}else {//进行注册
                        indexService.create(self.registerUser)
                            .then(
                                function (response) {
                                    self.registerOk();
                                    //注册成功进行登录操作
                                    angular.element('#hide_login_form').submit();
                                },
                                function (errResponse) {
                                    console.error('create error : ' + errResponse);
                                    self.registerOk();
                                }
                            )
					}
                },
				function (errResponse) {
					console.error(errResponse);
                    self.registerOk();
                }
			)
	}

	/*START提交登陆表单*/
	self.loginFormSubmit = function(){
		if (self.loginOrOther){
			self.loginLoading();
			var login = angular.element('#loginForm');
			login.attr('action',self.loginURL);
			login.attr('method','post');
            login.submit();
		}else {
            self.loginLoading();
            indexService.hasExistUsername(self.loginUser.email)
                .then(
                    function (response) {
                        console.log(response);
                        if (response == 'true'){
                            self.loginOrOther = true;
                            self.hideAllLoginError();
                            self.disLoginFormPasswordInput();
                            self.disLoginRem();
                            self.loginOK();
                        }else{
                            self.loginOrOther = false;
                            self.errorUserOREmail = true;
                            self.disLoginUserError();
                            self.hideLoginFormPasswordInput();
                            self.hideLoginRem();
                            self.loginOK();
                        }
                    },
                    function (errRespose) {
                        console.error(errRespose);
                        self.loginOK();
                    }
                )
		}
	}
	/*END提交登陆表单*/

	/*START 隐藏登陆表单所有错误信息*/
	self.hideAllLoginError = function () {
        self.errorUserOREmail = false;
        self.disLoginUserError();
        self.errorPassword = false;
        self.disLoginPasswordError();
    }
	/*END 隐藏登陆表单所有错误信息*/

	/*START 向服务器查看是否存在登录错误信息*/
	self.hasLoginErr = function () {
	    indexService.hasErrorMsg()
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
	    	self.hideRegisterForm();
	    	self.disLoginForm();
	        self.ifHasLoginError();
	    }else {
	    	self.disRegisterForm();
	    	self.hideLoginForm();
		}
	}

	//存在登录错误信息，隐藏登录按钮等
	self.ifHasLoginError = function () {
	    console.log("hasError")
	    self.errorPassword = true;
	    self.disLoginPasswordError();
	    self.hideLoginRem();
	    self.hideLoginFormBtn();
	}
	/*END 向服务器查看是否存在登录错误信息*/

	/*START 是否存在登录用户*/
	self.hasLoginUser = function () {
		indexService.hasLoginUser()
			.then(
				function (response) {
					console.log(response);
					if (response != ''){
                        self.isUserLogin = true;
						self.currentUser = response[0];
					}
                },
				function (errResponse) {
					console.error(errResponse);
                }
			)
    }
	/*END 是否存在登录用户*/

	/*START 显示userNav*/
	self.disUserNav = false;
	self.disOrHideNav = function () {
		self.disUserNav = !self.disUserNav;
    }
	/*END 显示userNav*/

	/*START 登录按钮 loading状态*/
	self.loginLoading = function () {
		var btn = angular.element('#loginBtn');
        btn[0].defaultValue = 'Loading';
        btn.attr('disabled', true);
    }

    //登录按钮 正常状态
	self.loginOK = function () {
        var btn = angular.element('#loginBtn');
        btn[0].defaultValue = '登录';
        btn.attr('disabled', false);
    }
	/*END 登录按钮 正常状态*/

	/*START 注册按钮loading*/
	self.registerLoading = function () {
		var btn = angular.element('#register');
        btn[0].defaultValue = 'Loading';
        btn.attr('disabled', true);
    }
    self.registerOk = function () {
        var btn = angular.element('#register');
        btn[0].defaultValue = '免费注册';
        btn.attr('disabled', false);
    }
	/*END 注册按钮loading*/

	//加载页面后调用方法
	self.hideFalseVedio();
	self.hasLoginErr();
	self.hasLoginUser();
}])