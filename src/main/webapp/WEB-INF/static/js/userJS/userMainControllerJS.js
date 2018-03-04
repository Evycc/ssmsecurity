'use strict';
app.controller('userMainController', ['$scope', 'userService', '$compile',
    function ($scope, userService, $compile) {
        var self = this;

        //当前用户email
        self.currentEmail = '';
        //当前用户username
        self.currentUsername = 'none';
        //当前用户角色名
        self.currentUserRole = 'none';

        //用户头像url
        self.user_head_url_prefix = '/static/png/';
        self.user_head_url_suffix = '';

        //登录url
        self.logout_url = '/logout';

        /*START操作页面*/

        //是否主页，或子页面
        self.currentPage = '';

        //子页面
        self.childPage = '';

        /**
         * 是否显示用户管理信息项
         * @type {boolean} true显示，反之隐藏
         */
        self.isShowUserPage = false;

        /**
         * 否显示个人资料页面
         * @type {boolean} true显示，反之隐藏
         */
        self.isShowUserInfoPage = false;

        /**
         * 是否显示更改密码页面
         * @type {boolean} true显示，反之隐藏
         */
        self.isShowEditPassword = false;

        //是否显示管理员信息项
        //为true显示，反之隐藏
        self.isShowRootPage = false;

        //是否显示发送邮件成功信息
        //为true显示，反之隐藏
        self.isShowSendMailSuccess = false;

        //显示发送更改密码请求的按钮
        //为true显示，反之隐藏
        self.isShowSendBtn = true;

        //显示用户管理页面
        //为true显示，反之隐藏
        self.isShowAllUser = false;

        //当前在线人数
        self.currentOnlineNum = 0;

        //显示在线用户页面
        //为true显示，反之隐藏
        self.isShowOnlineBox = false;

        //显示管理权限页面
        //为true显示，反之隐藏
        self.isShowRoleBox = false;
        /*END操作页面*/

        //定时器
        self.uploadTimer;

        //Jcrop
        var jcropApi;

        //存储截取图片坐标信息
        self.rectObj = {
            x: '',
            y: '',
            w: '',
            h: ''
        };

        /*start 上传表单元素*/
        var fd = new FormData();
        //logo
        var logoStr = "logo";
        //rect
        var rectStr = "rect";
        //username
        var usernameStr = "username";
        /*end 上传表单元素*/

        //用户个人资料表单
        self.userInfoForm = {
            username: '',
            email: '',
            //默认禁止修改用户电子邮箱
            emailDisabled: true,
            updateErrorMessage: 'none'
        };

        //分页信息
        self.pageModel = {
            showCount: '',
            totalCount: '',
            currentPage: '',
            currentResult: ''
        };
        //用户组
        self.userList = [];

        //在线用户组
        self.onlineUserList = [];

        //发送更改密码请求之前
        self.sendBefore = function () {
            self.isShowSendMailSuccess = false;
            self.isShowSendBtn = true;
        };

        //发送更改密码请求后
        self.sendSuccess = function () {
            self.isShowSendMailSuccess = true;
            self.isShowSendBtn = false;
        };

        //显示 更新用户资料错误信息
        self.disEditUserInfoErrorMessage = function () {
            var updateError = angular.element('#updateError');
            updateError.collapse('show');
        };

        //隐藏 更新用户资料错误信息
        self.hideEditUserInfoErrorMessage = function () {
            var updateError = angular.element('#updateError');
            updateError.collapse('hide');
        };

        $scope.$watch('ctrl.userInfoForm.username', function (newValue, oldValue) {
            self.hideEditUserInfoErrorMessage();
        });

        //显示首页
        self.disMainPage = function () {
            self.hideUserPage();
            self.hideRootPage();
        };

        //显示当前用户页面，用户信息页面或用户管理页面
        self.disCurrentPage = function () {
            if (self.currentPage == '用户信息') {
                self.hideUserPage();
                self.disUserPage();
            }
            if (self.currentPage == '用户管理') {
                self.hideRootPage();
                self.disRootPage();
            }
        };

        //显示用户信息信息页面
        self.disUserPage = function () {
            self.isShowUserPage = true;
            self.hideRootPage();
            self.currentPage = '用户信息';
            //隐藏用户信息子页面
            self.hideUserChildPage();
        };
        //显示用户管理页面
        self.disRootPage = function () {
            self.hideUserPage();
            self.isShowRootPage = true;
            self.currentPage = '用户管理';
            //隐藏用户管理子页面
            self.hideRootChildPage();
            //获取在线人数
            self.getOnlineNumber();
        };
        //隐藏用户信息页面
        self.hideUserPage = function () {
            self.isShowUserPage = false;
            self.currentPage = '';
            //隐藏页面下子页面
            self.hideUserChildPage();
        };
        //隐藏用户信息子页面
        self.hideUserChildPage = function () {
            self.hideUserInfoPage();
            self.hideEditPasswordPage()
        };
        //隐藏用户管理页面
        self.hideRootPage = function () {
            self.isShowRootPage = false;
            self.currentPage = '';
            //隐藏用户管理子页面
            self.hideRootChildPage();
        };
        //隐藏用户管理子页面
        self.hideRootChildPage = function () {
            self.hideAllUser();
            self.hideOnlineBox();
            self.hideRoleManage();
        };

        //父页面:用户信息
        //显示个人资料页面
        self.disUserInfoPage = function () {
            if (!self.isShowUserPage) {
                self.disUserPage();
            }
            self.hideRootChildPage();

            self.isShowUserInfoPage = true;
            self.isShowUserPage = false;
            self.childPage = '个人资料';

            //向服务器获取用户资料
            self.findUserInfo();
        };
        //父页面:用户信息
        //隐藏个人资料页面
        self.hideUserInfoPage = function () {
            self.isShowUserInfoPage = false;
            self.childPage = '';
        };

        //父页面:用户信息
        //显示更改密码页面
        self.disEditPasswordPage = function () {
            if (!self.isShowUserPage) {
                self.disUserPage();
            }
            self.hideRootChildPage();

            self.isShowEditPassword = true;
            self.isShowUserPage = false;
            self.childPage = '更改密码';
        };
        //父页面:用户信息
        //隐藏更改密码页面
        self.hideEditPasswordPage = function () {
            self.isShowEditPassword = false;
            self.childPage = '';
        };

        //父页面:用户管理
        //显示用户信息管理页面
        self.disAllUser = function () {
            if (!self.isShowRootPage) {
                self.disRootPage();
            }
            self.hideUserChildPage();

            self.isShowAllUser = true;
            self.isShowRootPage = false;
            self.childPage = '所有用户';

            //获取所有用户信息
            self.getAllUserInfo();
        };
        //父页面:用户管理
        //隐藏用户信息管理页面
        self.hideAllUser = function () {
            self.isShowAllUser = false;
            self.childPage = '';
        };

        //父页面:用户管理
        //显示用在线用户页面
        self.disOnlineBox = function () {
            if (!self.isShowRootPage) {
                self.disRootPage();
            }
            self.hideUserChildPage();

            self.isShowOnlineBox = true;
            self.isShowRootPage = false;
            self.childPage = '所有用户';

            //获取在线用户列表
            self.getOnlineUserList();
        };
        //父页面:用户管理
        //隐藏用在线用户页面
        self.hideOnlineBox = function () {
            self.isShowOnlineBox = false;
            self.childPage = '';
        };

        //父页面:用户管理
        //显示权限管理页面
        self.disRoleManagePage = function () {
            if (!self.isShowRootPage) {
                self.disRootPage();
            }
            self.hideUserChildPage();

            self.isShowRoleBox = true;
            self.isShowRootPage = false;
            self.childPage = '权限管理';

            //获取角色列表
            self.getRoleManage();
            //获取所有角色
            self.getAllRoleList();
            //初始化分页数据
            self.getRoleManage()
        };

        //父页面:用户管理
        //隐藏权限管理页面
        self.hideRoleManage = function () {
            self.isShowRoleBox = false;
            self.childPage = '';
        };

        //获取登录用户信息
        self.getLoginUser = function () {
            userService.getCurrentUserAndRole()
                .then(
                    function (response) {
                        self.currentUsername = response[0];
                        self.currentEmail = self.currentUsername;
                        self.currentUserRole = response[1];

                        //test
                        self.isJing();

                        self.getUserHeader();
                    },
                    function (errResponse) {
                        console.error(errResponse);
                    }
                )
        };

        //启用jcrop插件
        self.startImgJcrop = function () {
            var img_jcrop = angular.element('#upload_img');
            img_jcrop.Jcrop({
                boxWidth: 400,
                aspectRatio: 1,
                setSelect: [0, 0, 270, 270],    //设定选框初始位置
                onChange: showPreview,
                onSelect: showPreview
            }, function () {
                jcropApi = this;
            });
        };

        //获取截取图片坐标
        function showPreview(e) {
            self.rectObj.clear;
            self.rectObj.x = e.x;
            self.rectObj.y = e.y;
            self.rectObj.w = e.w;
            self.rectObj.h = e.h;
        };

        //获取用户上传图片
        self.getUploadFile = function () {
            clearInterval(self.uploadTimer);

            self.hideUploadError(); //隐藏上传文件错误信息

            var file = document.querySelector('input[type=file]').files[0];

            //上传文件是否为空
            if (file.size == 0) {
                self.disErrorFileEmpty();
                self.clearFileInput();
                return;
            }
            //上传文件大小不超过1M
            if (file.size > 1024 * 1024) {
                self.disErrorFileSize();
                self.clearFileInput();
                return;
            }
            //上传文件是否图片格式
            if (file.type.indexOf("image/") == -1) {
                self.disErrorNoImg();
                self.clearFileInput();
                return;
            }
            //上传文件是否图片格式
            var index = file.name.lastIndexOf('.');
            var suffix = file.name.substring(index + 1).toUpperCase();
            if (suffix != 'PNG' && suffix != 'GIF' && suffix != 'JPG') {
                self.disErrorFileFormat();
                self.clearFileInput();
                return;
            }

            /*START 本地预览*/
            var url;
            if (window.createObjectURL != undefined) {
                url = window.createObjectURL(file);
            } else if (window.URL != undefined) {
                // mozilla(firefox)
                url = window.URL.createObjectURL(file);
            } else if (window.webkitURL != undefined) {
                // webkit or chrome
                url = window.webkitURL.createObjectURL(file);
            }

            var upload_img = angular.element('#upload_img');
            upload_img.attr('src', url);
            /*END 本地预览*/

            if (typeof jcropApi != 'undefined') {
                jcropApi.destroy();
            }

            self.disUploadModal();

            //清空表单元素
            self.clearFd();

            fd.append(logoStr, file);

            self.clearFileInput();
        };

        //清空上传头像表单元素数据
        self.clearFd = function () {
            if (fd.has(logoStr)) {
                fd.delete(logoStr);
            }
            if (fd.has(rectStr)) {
                fd.delete(rectStr);
            }
            if (fd.has(usernameStr)) {
                fd.delete(usernameStr);
            }
        };

        /*START 上传用户头像*/
        self.uploadCutImage = function () {
            var str = 'x:' + self.rectObj.x + 'y:' + self.rectObj.y
                + 'w:' + self.rectObj.w + 'h:' + self.rectObj.h;
            fd.append(rectStr, str);
            fd.append(usernameStr, self.currentUsername);

            self.btnLoading('modal_btn');
            userService.uploadImage(fd)
                .then(
                    function (response) {
                        self.getUserHeader();
                        self.disUploadModal();

                        self.btnOk('modal_btn');
                    },
                    function (errResponse) {
                        console.error(errResponse)
                        alert(errResponse)
                        self.btnOk('modal_btn');
                    })
        };
        /*END 上传用户头像*/

        /*START 点击事件 上传文件*/
        self.clickUpload = function () {
            self.hideUploadError(); //隐藏上传文件错误信息
            var uploadInput = angular.element('#upload_hidden');

            uploadInput.click();
            self.uploadTimer = setInterval(function () {
                if (document.querySelector('input[type=file]').files[0]) {
                    self.getUploadFile();
                }
            }, 5);
        };
        /*END 点击事件 上传文件*/

        //显示截取图片模态框
        self.disUploadModal = function () {
            self.startImgJcrop();
            var modalDiv = angular.element('#uploadModal');
            modalDiv.modal('toggle', {
                keyboard: true
            });
        };

        /*START 上传错误信息*/
        //为true显示文件为空错误信息
        self.disErrorFileEmpty = function () {
            var div = angular.element('#file_empty');
            div.css('display', 'block');
        };
        //为true显示文件超过限定大小错误信息
        self.disErrorFileSize = function () {
            var div = angular.element('#file_max_size');
            div.css('display', 'block');
        };
        //为true显示文件格式不正确错误信息
        self.disErrorFileFormat = function () {
            var div = angular.element('#file_format');
            div.css('display', 'block');
        };
        //为true显示非图片格式错误信息
        self.disErrorNoImg = function () {
            var div = angular.element('#file_no_img');
            div.css('display', 'block');
        };
        /*END 上传错误信息*/

        /*START 隐藏上传文件错误信息*/

        //隐藏上传文件错误信息
        self.hideUploadError = function () {
            self.hideErrorFileEmpty();
            self.hideErrorFileSize();
            self.hideErrorFileFormat();
            self.hideErrorNoImg();
        };

        self.hideErrorFileEmpty = function () {
            var div = angular.element('#file_empty');
            div.css('display', 'none');
        };
        self.hideErrorFileSize = function () {
            var div = angular.element('#file_max_size');
            div.css('display', 'none');
        };
        self.hideErrorFileFormat = function () {
            var div = angular.element('#file_format');
            div.css('display', 'none');
        };
        self.hideErrorNoImg = function () {
            var div = angular.element('#file_no_img');
            div.css('display', 'none');
        };
        /*END 隐藏上传文件错误信息*/

        //清空file input文件
        self.clearFileInput = function () {
            var uploadInput = angular.element('#upload_hidden');
            uploadInput.after(uploadInput.clone().val(""));
            uploadInput.remove();
        };

        /*START 根据屏幕滚动，显示菜单条*/
        var scrollHeight = document.body.scrollTop;

        window.onscroll = function () {
            scrollHeight =
                document.documentElement.scrollTop || document.body.scrollTop;
            var nav = document.getElementById('nav_head');
            if (scrollHeight >= 200) {
                nav.style.display = 'block';
            } else {
                nav.style.display = 'none';
            }
        };
        self.liOnMouseOver = function (eleid) {
            var li = document.getElementById(eleid);
            setTimeout(
                function () {
                    var width = li.offsetWidth;
                    if (width == 74) {
                        if (eleid == 'liOne') {
                            li.innerText = "个人资料";
                        }
                        if (eleid == 'liTwo') {
                            li.innerText = "更改密码";
                        }
                        if (eleid == 'liThree') {
                            li.innerText = "退出登录";
                        }
                    }
                }, 1000);
        };
        self.liOnMouseOut = function (eleid) {
            var li = document.getElementById(eleid);
            li.innerText = ' ';
        };
        /*END 根据屏幕滚动，显示菜单条*/

        //设置用户头像
        self.toggleUserHeader = function () {
            var head1 = angular.element('#minHeadImg');
            var head2 = angular.element('#headImg');
            var head3 = angular.element('#big_head_box');

            head1.attr('src', self.user_head_url_prefix + self.user_head_url_suffix);
            head2.attr('src', self.user_head_url_prefix + self.user_head_url_suffix);
            head3.attr('src', self.user_head_url_prefix + self.user_head_url_suffix);
        };

        //获取用户头像
        self.getUserHeader = function () {
            userService.getUserHeaderId(self.currentUsername)
                .then(
                    function (response) {
                        self.user_head_url_suffix = response[0];

                        self.toggleUserHeader();
                    },
                    function (errResponse) {
                        console.error(errResponse);
                    }
                )
        };

        /*START loading状态按钮*/
        self.tempBtnText = '';

        self.btnLoading = function (btnId) {
            var btn = angular.element('#' + btnId);
            self.tempBtnText = btn[0].defaultValue;
            btn[0].defaultValue = 'Loading';
            btn.attr('disabled', true);
        };
        self.btnOk = function (btnId) {
            var btn = angular.element('#' + btnId);
            btn[0].defaultValue = self.tempBtnText;
            btn.attr('disabled', false);
        };
        /*END loading状态按钮*/

        //获取用户个人资料
        self.findUserInfo = function () {
            userService.getUserInfo(self.currentUsername)
                .then(
                    function (response) {
                        self.userInfoForm.username = response[0];
                        self.userInfoForm.email = response[1];
                    },
                    function (errResponse) {
                        console.error(errResponse);
                    }
                )
        };

        //更新用户个人资料
        self.editUserInfo = function () {
            self.hideEditUserInfoErrorMessage();
            userService.updateUserInfo(self.userInfoForm)
                .then(
                    function (response) {
                        alert("更新成功");
                        self.getLoginUser();
                        self.disUserInfoPage();
                    },
                    function (errResponse) {
                        console.error(errResponse);
                        if (errResponse != '') {
                            self.userInfoForm.updateErrorMessage = errResponse[0];
                            self.disEditUserInfoErrorMessage();
                        }
                    }
                )
        };

        //发送更改密码请求
        self.sendEditPassword = function () {
            self.btnLoading('send_edit_pass_btn');
            userService.editPassword(self.currentUsername)
                .then(
                    function (response) {
                        self.btnOk('send_edit_pass_btn');
                        self.sendSuccess();
                    },
                    function (errResponse) {
                        self.btnOk('send_edit_pass_btn');
                        self.sendBefore();
                        console.error(errResponse);
                    }
                )
        };

        //获取所有用户信息
        self.getAllUserInfo = function () {
            userService.getAllUser()
                .then(
                    function (response) {
                        console.log(response)
                        for (var key in response) {
                            self.formatPageStr(key);
                            self.userList = response[key];
                        }
                        //转换时间格式
                        self.formatNewTime(self.userList);
                        //添加分页ul
                        self.addLiByUl();
                    },
                    function (errResponse) {
                        console.error(errResponse);
                    }
                )
        };

        /*START 所有用户方法*/

        //解析获取的page对象字符串
        self.formatPageStr = function (pageStr) {
            var one = 'showCount=';
            var two = 'totalCount=';
            var three = 'currentPage=';
            var four = 'currentResult=';

            //获取showCount
            var index = pageStr.indexOf(one);
            var order = pageStr.indexOf(',');
            self.pageModel.showCount = pageStr.substring(index + one.length, order);
            //获取totalCount
            index = pageStr.indexOf(two);
            order = pageStr.indexOf(',', index);
            self.pageModel.totalCount = pageStr.substring(index + two.length, order);
            //获取currentPage
            index = pageStr.indexOf(three);
            order = pageStr.indexOf(',', index);
            self.pageModel.currentPage = pageStr.substring(index + three.length, order);
            //获取currentResult
            index = pageStr.indexOf(four);
            order = pageStr.indexOf('}');
            self.pageModel.currentResult = pageStr.substring(index + four.length, order);
        };

        //动态添加分页li
        self.addLiByUl = function () {
            //用户数量少于showCount，不显示分页ul
            if (parseInt(self.pageModel.totalCount) <= parseInt(self.pageModel.showCount)) {
                return;
            }

            var ulObj = angular.element('#dynamic_ul');
            ulObj.empty();

            var total = self.pageModel.totalCount / self.pageModel.showCount;

            var firstLi = "<li><a href='javascript:void(0)' ng-click='ctrl.firstLiClick()'>&laquo;</a></li>";
            var addFirstEle = $compile(firstLi)($scope);
            ulObj.append(addFirstEle);

            for (var i = 0; i < total; i++) {
                var currentIndex = i + 1;
                var li = '';
                var liId = 'li_';
                if (currentIndex == self.pageModel.currentPage) {
                    liId = liId + i;
                    li = "<li id='"
                        + liId
                        + "' class='active'><a href='javascript:void(0)' ng-click='ctrl.LiClickActive(\""
                        + liId + '\",\"' + currentIndex
                        + "\")'>"
                        + currentIndex + "</a></li>";
                } else {
                    liId = liId + i;
                    li = "<li id='"
                        + liId
                        + "'><a href='javascript:void(0)' ng-click='ctrl.LiClickActive(\""
                        + liId + '\",\"' + currentIndex
                        + "\")'>"
                        + currentIndex + "</a></li>";
                }
                var addLi = $compile(li)($scope);
                ulObj.append(addLi);
            }

            var lastLi = "<li><a href='javascript:void(0)' ng-click='ctrl.lastLiClick()'>&raquo;</a></li>";
            var addLastEle = $compile(lastLi)($scope);
            ulObj.append(addLastEle);
        };

        //设置转换格式后的时间
        self.formatNewTime = function (userList) {
            var d, year, month, day, hour, minuters;
            var str;
            for (var i = 0; i < userList.length; i++) {
                d = new Date(userList[i].lastTime);
                year = d.getFullYear();
                month = d.getMonth() + 1;
                day = d.getDate();
                hour = d.getHours();
                minuters = d.getMinutes();
                str = year + "/" + month + "/" + day + " " + hour + ":" + minuters;
                userList[i].lastTime = str;

                //test
                if (userList[i].ssoId == self.currentUsername){
                    self.currentEmail = userList[i].email;
                }
            }
        };

        //当前class为active的li元素的ID
        self.activeEleId = '';
        //li切换
        self.LiClickActive = function (id, currentPageId) {
            angular.element('#dynamic_ul li').removeClass('active');
            var li = angular.element('#' + id);
            li.addClass('active');

            self.activeEleId = id;

            self.pageModel.currentPage = currentPageId;
            self.getAllUserPage();
        };

        //前进到上一页
        self.firstLiClick = function () {
            if (self.pageModel.currentPage == 1 || self.pageModel.currentPage - 1 < 0) {
                return;
            }
            var index = self.pageModel.currentPage--;
            var ele = angular.element('#dynamic_ul li:eq(' + index + ')');
            var eleId = ele.attr('id');
            self.LiClickActive(eleId, self.pageModel.currentPage);
        };

        //前进到下一页
        self.lastLiClick = function () {
            var t1 = parseInt(self.pageModel.totalCount / self.pageModel.showCount);
            var t2 = self.pageModel.totalCount / self.pageModel.showCount;

            var total = t1 >= t2 ? t1 : t1 + 1;
            if (self.pageModel.currentPage == total) {
                return;
            }

            var index = self.pageModel.currentPage++;
            var ele = angular.element('#dynamic_ul li:eq(' + index + ')');
            var eleId = ele.attr('id');
            self.LiClickActive(eleId, self.pageModel.currentPage);
        };

        //分页获取用户信息
        self.getAllUserPage = function () {
            userService.getAllUserPage(self.pageModel)
                .then(
                    function (response) {
                        for (var key in response) {
                            self.formatPageStr(key);
                            self.userList = response[key];
                        }
                        //转换时间格式
                        self.formatNewTime(self.userList);
                        //添加分页ul
                        self.addLiByUl();
                    },
                    function (errResponse) {
                        console.error(errResponse);
                    }
                )
        };

        //锁定用户账号
        self.lockedUserById = function (userId, lockedUsername) {
            console.log(lockedUsername)
            if (self.currentUsername == lockedUsername) {
                self.disWarningAlert('不可以选择锁定当前用户。');
                return;
            }
            userService.lockedUser(userId)
                .then(
                    function (response) {
                        //锁定成功更新用户信息列表
                        self.beforeUpdateSuccess();
                        self.disSuccessAlert('锁定用户成功。');
                    },
                    function (errResponse) {
                        var str = errResponse;
                        if (str.indexOf('Method Not Allowed') > -1) {
                            self.disWarningAlert('必须拥有管理员权限才可执行。');
                        }
                        console.error(errResponse);
                        self.disWarningAlert(errResponse);
                    }
                )
        };

        //解锁用户账号
        self.unLockUserById = function (userId) {
            userService.unlockUser(userId)
                .then(
                    function (response) {
                        //解锁成功更新用户信息列表
                        self.beforeUpdateSuccess();
                        self.disSuccessAlert('解锁用户成功。');
                    },
                    function (errResponse) {
                        var str = errResponse;
                        if (str.indexOf('Method Not Allowed') > -1) {
                            self.disWarningAlert('必须拥有管理员权限才可执行。');
                        }
                        console.error(errResponse);
                        self.disWarningAlert(errResponse);
                    }
                )
        };

        //删除用户账号
        self.deleteUserById = function (userId, delUsername) {
            if (self.currentUsername == delUsername) {
                self.disWarningAlert('不可以选择删除当前用户。');
                return;
            }
            userService.deleteUser(userId)
                .then(
                    function (response) {
                        //删除成功更新用户信息列表
                        self.getAllUserInfo();
                        self.disSuccessAlert('删除用户成功。');
                    },
                    function (errResponse) {
                        var str = errResponse;
                        console.error(errResponse);
                        self.disWarningAlert(errResponse);
                    }
                )
        };

        /*start alert*/
        self.disWarningAlert = function (msg) {
            var div = angular.element('#user_box');
            var s = '警告！';
            if (msg != '') {
                s = msg;
            }

            var waringAlert =
                "<div class='col-xs-2 alert alert-warning fade in alert_div'>" +
                "<a href='' class='close' ng-click='ctrl.hideAlert()'>&times;</a>" +
                "<strong>" +
                s +
                "</strong>"
            "</div>";

            var alertEle = $compile(waringAlert)($scope);
            div.append(alertEle);
        };
        self.hideAlert = function () {
            var alertOne = angular.element('.alert');
            alertOne.alert('close');
        };
        self.disSuccessAlert = function (msg) {
            var div = angular.element('#user_box');
            var s = '成功！';
            if (msg != '') {
                s = msg;
            }

            var successAlert =
                "<div class='col-xs-2 alert alert-success fade in alert_div'>" +
                "<a href='' class='close' ng-click='ctrl.hideAlert()'>&times;</a>" +
                "<strong>" +
                s +
                "</strong>"
            "</div>";

            var alertEle = $compile(successAlert)($scope);
            div.append(alertEle);
        };
        /*end alert*/

        //更新用户后重新获取用户列表
        self.beforeUpdateSuccess = function () {
            if (self.pageModel != '') {
                self.getAllUserPage();
            } else {
                self.getAllUserInfo();
            }
        };

        //获取在线人数
        self.getOnlineNumber = function () {
            userService.getOnlineNum()
                .then(
                    function (response) {
                        self.currentOnlineNum = response;
                    },
                    function (errResponse) {
                        console.error(errResponse);
                    }
                )
        };

        //获取在线用户列表
        self.getOnlineUserList = function () {
            userService.getOnlineUsers()
                .then(
                    function (response) {
                        self.onlineUserList = response;
                        self.formatNewTime(self.onlineUserList);
                        self.addOnlineUserPageUl();
                        self.onlineUserListPageArray();
                    },
                    function (errResponse) {
                        console.error(errResponse);
                    }
                )
        };

        //踢出用户
        self.kickoutUserByEmail = function (email, username) {
            if (username == self.currentUsername) {
                self.disSuccessAlert('不可踢出当前用户');
                return;
            }
            userService.kickoutUser(email)
                .then(
                    function (response) {
                        self.disSuccessAlert('踢出用户成功。');
                        self.getOnlineNumber();
                        //重新获取在线用户
                        self.getOnlineUserList();
                    },
                    function (errResponse) {
                        var str = errResponse;
                        console.error(errResponse);
                    }
                )
        };

        /*START 在线用户分页*/

        //每页显示数量  默认为3
        self.onlinePageShowCount = 3;
        //当前显示页 默认为1
        self.onlinePageCurrentPage = 1;
        //slice 的 start
        self.onlinePageResult = 0;
        self.addOnlineUserPageUl = function () {
            //用户数少于每页显示数量，不添加分页ul
            if (self.onlineUserList.length <= self.onlinePageShowCount) {
                return;
            }

            var ulObj = angular.element('#online_page_ul');
            ulObj.empty();

            var total = self.onlineUserList.length / self.onlinePageShowCount;

            var firstLi = "<li><a href='javascript:void(0)' ng-click='ctrl.onlineFirstLiClick()'>&laquo;</a></li>";
            var addFirstEle = $compile(firstLi)($scope);
            ulObj.append(addFirstEle);

            for (var i = 0; i < total; i++) {
                var currentIndex = i + 1;
                var li = '';
                var liId = 'online_li_';
                if (currentIndex == self.onlinePageCurrentPage) {
                    liId = liId + i;
                    li = "<li id='"
                        + liId
                        + "' class='active'><a href='javascript:void(0)' ng-click='ctrl.onlineLiClickActive(\""
                        + liId + '\",\"' + currentIndex
                        + "\")'>"
                        + currentIndex + "</a></li>";
                } else {
                    liId = liId + i;
                    li = "<li id='"
                        + liId
                        + "'><a href='javascript:void(0)' ng-click='ctrl.onlineLiClickActive(\""
                        + liId + '\",\"' + currentIndex
                        + "\")'>"
                        + currentIndex + "</a></li>";
                }
                var addLi = $compile(li)($scope);
                ulObj.append(addLi);
            }

            var lastLi = "<li><a href='javascript:void(0)' ng-click='ctrl.onlineLastLiClick()'>&raquo;</a></li>";
            var addLastEle = $compile(lastLi)($scope);
            ulObj.append(addLastEle);
        };

        //当前class为active的li元素的ID
        self.onlineactiveEleId = '';
        //li切换
        self.onlineLiClickActive = function (id, currentPageId) {
            angular.element('#online_page_ul li').removeClass('active');
            var li = angular.element('#' + id);
            li.addClass('active');

            self.onlineactiveEleId = id;

            self.onlinePageCurrentPage = currentPageId;
            self.onlinePageResult = (self.onlinePageCurrentPage - 1) * self.onlinePageShowCount;

            self.onlineUserListPageArray();
        };

        //前进到上一页
        self.onlineFirstLiClick = function () {
            if (self.onlinePageCurrentPage == 1 || self.onlinePageCurrentPage - 1 < 0) {
                return;
            }

            var index = --self.onlinePageCurrentPage;
            var ele = angular.element('#online_page_ul li:eq(' + index + ')');
            var eleId = ele.attr('id');

            self.onlineLiClickActive(eleId, self.onlinePageCurrentPage);
        };

        //前进到下一页
        self.onlineLastLiClick = function () {
            var t1 = parseInt(self.onlineUserList.length / self.onlinePageShowCount);
            var t2 = self.onlineUserList.length / self.onlinePageShowCount;
            var total = t1 >= t2 ? t1 : t1 + 1;

            if (self.onlinePageCurrentPage == total) {
                return;
            }

            var index = ++self.onlinePageCurrentPage;
            var ele = angular.element('#online_page_ul li:eq(' + index + ')');
            var eleId = ele.attr('id');

            self.onlineLiClickActive(eleId, self.onlinePageCurrentPage);
        };

        //分段数组
        self.userListPage;
        self.onlineUserListPageArray = function () {
            if (self.onlinePageResult > self.onlineUserList.length) {
                self.userListPage = self.onlineUserList;
                return;
            }
            self.userListPage = self.onlineUserList.slice(self.onlinePageResult);
        };

        /*END 在线用户分页*/

        /*START 权限管理方法*/

        /**
         * 用户-角色关联map
         */
        self.ManageMap;
        /**
         * 所有角色
         */
        self.roleList;

        //获取用户-角色关联map
        self.getRoleManage = function () {
            userService.getUserRole()
                .then(
                    function (response) {
                        if (response != '') {
                            self.ManageMap = response;
                            //分页
                            self.addRoleManagePageUl();
                        }
                    },
                    function (errResponse) {
                        console.error(errResponse);
                    }
                )
        };
        //获取所有角色数组
        self.getAllRoleList = function () {
            userService.getAllRole()
                .then(
                    function (response) {
                        if (response != '') {
                            self.roleList = response;
                        }
                    },
                    function (errResponse) {
                        console.error(errResponse);
                    }
                )
        };
        //添加选中的角色
        self.addRoleMap = {
            username: '',
            roles: [],
        };
        //提交添加角色
        self.addRoleByUsername = function (selId, username) {
            var sel = angular.element('#' + selId).val();
            if (sel.length == 0 || sel == '') {
                self.disWarningAlert('请选择角色。');
                return;
            }
            self.addRoleMap.username = username;
            self.addRoleMap.roles = sel;

            self.v = self.ManageMap[username];
            for (var i = 0; i < self.v.length; i++) {
                for (var j = 0; j < self.addRoleMap.roles.length; j++) {
                    if (self.v[i].description == self.addRoleMap.roles[j]) {
                        self.disWarningAlert('用户已存在该角色。');
                        return;
                    }
                }
            }
            userService.addUserRole(self.addRoleMap)
                .then(
                    function (response) {
                        self.disRoleManagePage();
                        self.disSuccessAlert('添加角色成功。');
                    },
                    function (errResponse) {
                        var str = errResponse;
                        console.error(errResponse);
                        self.disWarningAlert(errResponse);
                    }
                )
        };
        //提交删除角色
        self.delRoleByUsername = function (selId, username) {
            var sel = angular.element('#' + selId).val();
            if (sel.length == 0 || sel == '') {
                self.disWarningAlert('请选择角色。');
                return;
            }
            self.addRoleMap.username = username;
            self.addRoleMap.roles = sel;
            self.v = self.ManageMap[username];
            self.hasRole = false;

            for (var i = 0; i < self.v.length; i++) {
                for (var j = 0; j < self.addRoleMap.roles.length; j++) {
                    if (self.v[i].description == self.addRoleMap.roles[j]) {
                        self.hasRole = true;
                        break;
                    }
                }
            }

            if (!self.hasRole) {
                self.disWarningAlert('用户不存在该角色。');
                return;
            }

            userService.delUserRole(self.addRoleMap)
                .then(
                    function (response) {
                        self.disRoleManagePage();
                        self.disSuccessAlert('删除角色成功。');
                    },
                    function (errResponse) {
                        var str = errResponse;
                        console.error(errResponse);
                        self.disWarningAlert(errResponse);
                    }
                )
        };


        /*START 用户管理分页ul*/

        //每页显示数量  默认为3
        self.rolePageShowCount = 3;
        //当前显示页 默认为1
        self.rolePageCurrentPage = 1;
        //slice 的 start
        self.rolePageResult = 0;
        //分段map
        self.roleMapPage = {};

        //初始化分页数据
        self.pageInit = function () {
            self.rolePageShowCount = 3;
            self.rolePageCurrentPage = 1;
            self.rolePageResult = 0;
            self.roleMapPage = {};
        };

        //分段获取map
        self.roleManagePageMap = function () {
            self.roleMapPage = {};
            var i = 0;
            var count = 0;
            for (var key in self.ManageMap) {
                if (i >= self.rolePageResult) {
                    self.roleMapPage[key] = self.ManageMap[key];
                    count++;
                    if (count == self.rolePageShowCount) {
                        break;
                    }
                }
                i++;
            }
            console.log(self.roleMapPage)
        };

        //添加分页ul
        self.addRoleManagePageUl = function () {
            self.roleManagePageMap();

            //用户数少于每页显示数量，不添加分页ul
            if (self.roleMapPage.length <= self.rolePageShowCount) {
                return;
            }

            var ulObj = angular.element('#role_page_ul');
            ulObj.empty();

            var total = self.getMapLength(self.ManageMap) / self.rolePageShowCount;

            var firstLi = "<li><a href='javascript:void(0)' ng-click='ctrl.roleFirstLiClick()'>&laquo;</a></li>";
            var addFirstEle = $compile(firstLi)($scope);
            ulObj.append(addFirstEle);

            for (var i = 0; i < total; i++) {
                var currentIndex = i + 1;
                var li = '';
                var liId = 'role_li_';
                if (currentIndex == self.rolePageCurrentPage) {
                    liId = liId + i;
                    li = "<li id='"
                        + liId
                        + "' class='active'><a href='javascript:void(0)' ng-click='ctrl.roleLiClickActive(\""
                        + liId + '\",\"' + currentIndex
                        + "\")'>"
                        + currentIndex + "</a></li>";
                } else {
                    liId = liId + i;
                    li = "<li id='"
                        + liId
                        + "'><a href='javascript:void(0)' ng-click='ctrl.roleLiClickActive(\""
                        + liId + '\",\"' + currentIndex
                        + "\")'>"
                        + currentIndex + "</a></li>";
                }
                var addLi = $compile(li)($scope);
                ulObj.append(addLi);
            }

            var lastLi = "<li><a href='javascript:void(0)' ng-click='ctrl.roleLastLiClick()'>&raquo;</a></li>";
            var addLastEle = $compile(lastLi)($scope);
            ulObj.append(addLastEle);
        };

        //当前class为active的li元素的ID
        self.roleActiveEleId = '';
        //li切换
        self.roleLiClickActive = function (id, currentPageId) {
            angular.element('#role_page_ul li').removeClass('active');
            var li = angular.element('#' + id);
            li.addClass('active');

            self.roleActiveEleId = id;

            self.rolePageCurrentPage = currentPageId;
            self.rolePageResult = (self.rolePageCurrentPage - 1) * self.rolePageShowCount;

            self.roleManagePageMap();
        };

        //前进到上一页
        self.roleFirstLiClick = function () {
            if (self.rolePageCurrentPage == 1 || self.rolePageCurrentPage - 1 < 0) {
                return;
            }

            var index = --self.rolePageCurrentPage;
            var ele = angular.element('#role_page_ul li:eq(' + index + ')');
            var eleId = ele.attr('id');

            self.roleLiClickActive(eleId, self.rolePageCurrentPage);
        };

        //前进到下一页
        self.roleLastLiClick = function () {
            var t1 = parseInt(self.getMapLength(self.ManageMap) / self.rolePageShowCount);
            var t2 = self.getMapLength(self.ManageMap) / self.rolePageShowCount;
            var total = t1 >= t2 ? t1 : t1 + 1;

            if (self.rolePageCurrentPage == total) {
                return;
            }

            var index = ++self.rolePageCurrentPage;
            var ele = angular.element('#role_page_ul li:eq(' + index + ')');
            var eleId = ele.attr('id');

            self.roleLiClickActive(eleId, self.rolePageCurrentPage);
        };

        //获取map长度
        self.getMapLength = function (map) {
            var count = 0;
            for (var key in map) {
                count++;
            }
            return count;
        };
        /*END 用户管理分页ul*/

        /*END 权限管理方法*/

        /*END 所有用户方法*/

        /*start websocket*/
        var websocket;

        /**
         * 发送消息体
         * @type {{from: string, to: string, message: string}}
         */
        self.messageModel = {
            from: '',
            to: '',
            message: ''
        };
        /**
         * 存储着用户在线或离线状态的map
         * @type {{cur: string, isOnline: boolean}}
         * {cur : 当前对话用户
         * isOnline : 在线为true，反之为false}
         */
        self.wsUserIsOnline = {
            cur: '',
            isOnline: false
        };
        /**
         * 普通消息标识
         * @type {string}
         */
        const WS_ORDINRY = 'ws-ordinary';
        /**
         * 离线消息标识
         * @type {string}
         */
        const WS_OFFLINE = 'ws-offline';
        /**
         * 在线标识
         * @type {string}
         */
        const WS_ISONLINE = 'ws-isOnline';
        /**
         * 离线标识
         * @type {string}
         */
        const WS_ISOFFLINE = 'ws-isOffline';
        /**
         * 用户断开连接或超时标识
         * @type {string}
         */
        const WS_OVERTIME = 'ws-overtime';

        const ws_url_prefix = 'ws://localhost:8080/websocket';
        const sockJS_url_prefix = 'http://localhost:8080/sockjs/websocket';

        if ('WebSocket' in window) {
            websocket = new WebSocket(ws_url_prefix);
        } else if ('MozWebSocket' in window) {
            websocket = new MozWebSocket(ws_url_prefix);
        } else {
            websocket = new SockJS(sockJS_url_prefix);
        }
        websocket.onopen = function (evnt) {
            console.log("链接服务器成功!");
        };
        websocket.onmessage = function (evnt) {
            //将收到的json语句转换为对象（键值对）
            var obj = eval('(' + evnt.data + ')');
            //test
            console.log(obj)
            //处理消息
            self.messageHandler(obj);
        };
        websocket.onerror = function (evnt) {
            console.error(evnt);
        };
        websocket.onclose = function (evnt) {
            console.log("与服务器断开了链接!");
        };

        /**
         * 消息处理器
         * @param obj   键值对 key为消息标识，value为消息正体
         */
        self.messageHandler = function (obj) {
            for (var key in obj) {
                //普通消息
                if (key == WS_ORDINRY) {
                    self.buildOrdinaryMessage(obj[key]);
                }
                //离线消息
                if (key == WS_OFFLINE){
                    self.buildOrdinaryMessage(obj[key]);
                }
                //在线标识
                if (key == WS_ISONLINE) {
                    if (self.wsUserIsOnline.cur == obj[key]) {
                        //将当前对话用户设置为在线状态
                        self.wsUserIsOnline.isOnline = true;
                        self.disOnlineOrOffline();
                    }
                }
                //离线标识
                if (key == WS_ISOFFLINE) {
                    if (self.wsUserIsOnline.cur == obj[key]) {
                        //将当前对话用户设置为离线状态
                        self.wsUserIsOnline.isOnline = false;
                        self.disOnlineOrOffline();
                    }
                }
                if (key == WS_OVERTIME){
                    confirm("与服务器断开了连接，请重新登录。")
                    location.href = self.logout_url;
                }
            }
        };
        /**
         * 更改当前对话用户的在线状况
         */
        self.disOnlineOrOffline = function () {
            var span = angular.element('#online_box');
            var text = angular.element('#online_text')[0];
            if (self.wsUserIsOnline.isOnline) {
                span.css('background-color', '#3ca013');
                text.innerText = '在线';
            } else {
                span.css('background-color', '#d9534f');
                text.innerText = '离线';
            }
        };

        /**
         * 普通消息处理
         * @param obj   消息正体<br>
         * {from : 消息发送人
         * to : 消息接收人
         * message : 消息
         * date : 日期
         * }
         */
        self.buildOrdinaryMessage = function (obj) {
            var from = obj.from;
            var to = obj.to;

            if (from == to) {
                //发送人与收信人相同，不进行存储
                return;
            }

            var message = obj.message;
            var date = obj.date;

            var html = self.buildOrdinaryMessageHtml(from, to, message, date);

            self.appendMsgDiv(html);
        };

        /**
         * 构建普通消息html语句
         * @param from  消息发送人
         * @param to    消息接收人
         * @param message   消息
         * @param date  日期
         * @return  返回构造完成的html语句
         */
        self.buildOrdinaryMessageHtml = function (from, to, message, date) {
            //处理日期
            var h_date = new Date(parseInt(date));
            var week;
            var time = self.buildMessageTime(h_date);

            var isLongMsg = 'display:inline-block;';
            if (message.length > 40) {
                isLongMsg = 'display:block;width: 100%;';
            }

            var html = '';

            if (from == self.currentEmail) {
                html = '<div class="row message_row">' +
                    '<div class="message_row message_date">' +
                    time +
                    '</div>' +
                    '<div class="message_from msg_box" style=\"' +
                    isLongMsg +
                    '\">' +
                    message +
                    '</div>' +
                    '</div>';
            } else {
                html = '<div class="row message_row">' +
                    '<div class="message_row message_date">' +
                    time +
                    '</div>' +
                    '<div class="message_to msg_box" style=\"' +
                    isLongMsg +
                    '\">' +
                    message +
                    '</div>' +
                    '</div>';
            }

            return html;
        }

        /**
         * 构造消息的时间
         * @param d 发送消息的时间
         * @return  返回构造的字符串
         */
        self.buildMessageTime = function (d) {
            //消息超过一天，显示星期
            var disWeek = ((new Date().getTime() - d.getTime()) / (1000 * 60 * 60)) > 24 ? true : false;
            var hours = d.getHours();
            var minutes = d.getMinutes();
            var week = '';
            var time = hours <= 12 ? '上午 ' + hours + ':' + minutes
                : '下午' + hours + ':' + minutes;

            if (disWeek) {
                switch (d.getUTCDay()) {
                    case 0:
                        week = '星期日';
                        break;
                    case 1:
                        week = '星期一';
                        break;
                    case 2:
                        week = '星期二';
                        break;
                    case 3:
                        week = '星期三';
                        break;
                    case 4:
                        week = '星期四';
                        break;
                    case 5:
                        week = '星期五';
                        break;
                    case 6:
                        week = '星期六';
                        break;
                }

                time = week + ' ' + time;
            }

            return time;
        }

        /**
         * 发送信息给单个用户
         */
        self.sendToUser = function () {
            var messageBox = angular.element('#input_message')[0];
            if (messageBox.value.search('[\\S]') < 0) {
                //不能发送空白字符
                alert('不能发送空白字符');
                return;
            }
            if (self.messageModel.from != self.currentUsername) {
                self.messageModel.from = self.currentUsername;
            }
            self.messageModel.message = messageBox.value;
            //清空消息框
            messageBox.value = '';

            userService.sendToUser(self.messageModel)
                .then(
                    function (response) {
                        console.log('send success' + response);
                        self.disSendMessagge(self.messageModel.message);
                    },
                    function (errResponse) {
                        console.error(errResponse);
                    }
                )
        };

        /**
         * 显示发出的消息
         * @param m 消息
         */
        self.disSendMessagge = function (m) {
            var time = self.buildMessageTime(new Date());
            var isLongMsg = 'display:inline-block;';
            if (m.length > 40) {
                isLongMsg = 'display:block;width: 100%;';
            }

            var html = '<div class="row message_row">' +
                '<div class="message_row message_date">' +
                time +
                '</div>' +
                '<div class="message_from msg_box" style=\"' +
                isLongMsg +
                '\">' +
                m +
                '</div>' +
                '</div>';

            self.appendMsgDiv(html);
        };

        /**
         * 添加信息到对话框
         * @param html  对话的html语句
         */
        self.appendMsgDiv = function (html) {
            var msgDiv = angular.element('#message_body');
            msgDiv.append(html);
            msgDiv[0].scrollTop = msgDiv[0].scrollHeight;
        }

        /**
         * 清空对话框
         */
        self.clearMsgDiv = function () {
            var msgDiv = angular.element('#message_body')[0];
            msgDiv.innerHTML = '';
        };

        /**
         * 获取from用户对应to用户的离线信息
         * @param from  接收离线信息的用户
         * @param to    发送离线信息的用户
         */
        self.getOfflineMessage = function (from, to) {
            var map = {
                from: from,
                to: to
            };

            userService.getOfflineMessage(map)
                .then(
                    function (response) {
                        console.log('get offline msg : ' + response);
                    },
                    function (errResponse) {
                        console.error(errResponse);
                    }
                );
        };

        /**
         * 监听窗口关闭事件
         */
        window.onbeforeunload = function () {
            userService.isOnlineByUsername(self.currentUsername)
                .then(
                    function (response) {
                        if (response == false) {
                            if (websocket != '' || websocket != undefined) {
                                //关闭websocket连接
                                websocket.close();
                            }
                        }
                    },
                    function (errResponse) {
                        console.error(errResponse);
                    }
                )
        };
        /*end websocket*/

        /**
         * 输入框获得焦点事件
         */
        self.messageFocus = function () {
            angular.element('#message_footer').css('background', '#fff');
            angular.element('#input_message').css('background', '#fff');
        };
        /**
         * 输入框失去焦点事件
         */
        self.messageBlur = function () {
            angular.element('#message_footer').css('background', '#e4d7d7');
            angular.element('#input_message').css('background', '#e4d7d7');
        };

        /**
         * 显示对话框
         * @param toEmail   收信人邮箱
         */
        self.disMessageModal = function (toEmail) {
            if (toEmail == '') {
                console.error('收信人为空!');
                return;
            }
            //设置为当前对话用户
            self.wsUserIsOnline.cur = toEmail;

            self.messageModel.to = toEmail;
            //获取收信人头像
            var to_img = angular.element('#to_header_img');
            userService.getUserHeaderId(toEmail)
                .then(
                    function (response) {
                        to_img.attr('src', self.user_head_url_prefix + response[0]);
                    },
                    function (errResponse) {
                        console.error(errResponse);
                    }
                );

            var modalDiv = angular.element('#Message_Modal');
            modalDiv.modal('toggle', {
                keyboard: true
            });

            //清空对话框
            self.clearMsgDiv();
            //接收离线信息
            self.getOfflineMessage(self.currentUsername, toEmail);
        };

        /**
         * 回车发送信息
         * @param $event    按下的键值
         */
        self.enterSend = function ($event) {
            if ($event.keyCode == 13) {
                self.sendToUser();
            }
        }

        /**
         * 页面失效定时器
         */
        self.sessionTimer;
        /**
         * 主动式session失效管理
         */
        self.getSessionExpire = function () {
            userService.getSessionTime()
                .then(
                    function (response) {
                        if (response > 0){
                            //设置session失效定时器
                            self.setSessionExpireTimer(response);
                        }
                    },
                    function (errResponse) {
                        console.error(errResponse);
                    }
                )
        };

        /**
         * session失效处理
         */
        self.sessionExpire = function () {
            userService.sessionExpireInterval()
                .then(
                    function (response) {
                        console.log(response)
                        if (response > 0){
                            //session尚未失效
                            self.setSessionExpireTimer(response);
                        } else{
                            //session失效
                            confirm("页面已过期，请重新登录。");
                            websocket.close();
                            location.href = "/logout";
                        }
                    },
                    function (errResponse) {
                        console.error(errResponse);
                    }
                )
        };
        /**
         * 设置session失效处理定时器
         * @param t
         */
        self.setSessionExpireTimer = function (t) {
            self.clearSessionExpireTimer();
            self.sessionTimer = setTimeout(function(){
                self.sessionExpire();
            }, t * 1000);
        };
        /**
         * 清除session失效定时器
         */
        self.clearSessionExpireTimer = function () {
            if (self.sessionTimer != undefined){
                clearTimeout(self.sessionTimer);
            }
        };

        /*start jing*/
        self.loveday = 0;
        self.knowday = 0;
        self.msgJing = "微风轻轻吹起，我好喜欢你。JING，在大学期间遇到你，可能花光了我所有的运气吧。嗯，我总能等到姗姗来迟的你，希望以后我们还能这样继续的走下去。" +
            "想对你说的话还有好多好多噢，嗯，以后再好好的说给你听好了。很喜欢很喜欢你的泰。";
        self.initLoveDay = function () {
            var lastDay = new Date();
            var startDay = new Date();
            startDay.setFullYear(2017);
            startDay.setMonth(6);
            startDay.setDate(24);

            self.loveday = (lastDay - startDay) / (24 * 60 * 60 * 1000);
        };
        self.initKnowDay = function () {
            var lastDay = new Date();
            var startDay = new Date();
            startDay.setFullYear(2017);
            startDay.setMonth(1);
            startDay.setDate(16);

            self.knowday = (lastDay - startDay) / (24 * 60 * 60 * 1000);
        };
        self.disJing = function () {
            self.initLoveDay();
            self.initKnowDay();

            var modal = angular.element('#loveJing');
            modal.modal('toggle', {
                keyboard: true
            });
        };
        self.closeJing = function () {
            var div = angular.element('#heart_div');
            div.css('display', 'block');
            div.addClass('heartAnimation');
            setTimeout(function () {
                var div = angular.element('#heart_div');
                div.css('display', 'none');
                div.attr('css', 'heart');
            }, 1000)
        };
        self.isJing = function () {
            if (self.currentUsername.indexOf('1173441689@qq.com') > -1) {
                self.disJing();
            }
        };
        /*end jing*/

        //获取登录用户信息
        self.getLoginUser();
        //设置页面过期时间
        self.getSessionExpire();
    }]);