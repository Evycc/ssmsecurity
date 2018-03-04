/*
Navicat MySQL Data Transfer

Source Server         : evy_se
Source Server Version : 50720
Source Host           : localhost:3306
Source Database       : evy_se

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2017-11-18 16:44:49
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `persistent_logins`
-- ----------------------------
DROP TABLE IF EXISTS `persistent_logins`;
CREATE TABLE `persistent_logins` (
  `username` varchar(64) NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of persistent_logins
-- ----------------------------

-- ----------------------------
-- Table structure for `se_permission`
-- ----------------------------
DROP TABLE IF EXISTS `se_permission`;
CREATE TABLE `se_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '权限id',
  `permission_name` varchar(32) NOT NULL COMMENT '权限名称',
  `permission_sign` varchar(32) NOT NULL COMMENT '权限标识，程序中判断使用',
  `description` varchar(128) NOT NULL COMMENT '权限描述，UI界面使用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='权限表';

-- ----------------------------
-- Records of se_permission
-- ----------------------------
INSERT INTO `se_permission` VALUES ('1', 'user:create', 'user:create', '添加用户');

-- ----------------------------
-- Table structure for `se_role`
-- ----------------------------
DROP TABLE IF EXISTS `se_role`;
CREATE TABLE `se_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '表主键',
  `type` varchar(32) NOT NULL COMMENT '角色',
  `description` varchar(64) NOT NULL DEFAULT '' COMMENT '角色描述，用户前端显示',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of se_role
-- ----------------------------
INSERT INTO `se_role` VALUES ('1', 'ROOT', '管理员');
INSERT INTO `se_role` VALUES ('2', 'USER', '用户');

-- ----------------------------
-- Table structure for `se_role_permission`
-- ----------------------------
DROP TABLE IF EXISTS `se_role_permission`;
CREATE TABLE `se_role_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '表id',
  `role_id` int(11) NOT NULL COMMENT '角色id',
  `permission_id` int(11) NOT NULL COMMENT '权限id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色-权限表';

-- ----------------------------
-- Records of se_role_permission
-- ----------------------------

-- ----------------------------
-- Table structure for `se_user`
-- ----------------------------
DROP TABLE IF EXISTS `se_user`;
CREATE TABLE `se_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '表主键',
  `email` varchar(64) DEFAULT NULL COMMENT '邮箱',
  `sso_id` varchar(64) DEFAULT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '密码',
  `enable` varchar(32) DEFAULT 'true' COMMENT '设置true启用用户',
  `account` varchar(32) DEFAULT 'true' COMMENT '设置true用户账户未过期',
  `credentials` varchar(32) DEFAULT 'true' COMMENT '设置true凭证尚未过期',
  `locked` varchar(32) DEFAULT 'true' COMMENT '设置true账户未锁定',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_time` timestamp NULL DEFAULT NULL,
  `salt` varchar(64) DEFAULT NULL COMMENT '盐',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of se_user
-- ----------------------------
INSERT INTO `se_user` VALUES ('39', '627935110@qq.com', '627935110', 'x2aUtUIQ2qrENcc5b3q66A==', 'true', 'true', 'true', 'true', '2017-10-12 22:40:53', '2017-11-17 06:28:59', '627935110@qq.com');
INSERT INTO `se_user` VALUES ('43', '2036758792@qq.com', '2036758792@qq.com', '3uSkivujGFJjCI2EybUSvQ==', 'true', 'true', 'true', 'true', '2017-10-05 14:34:21', '2017-10-22 05:30:53', '2036758792@qq.com');
INSERT INTO `se_user` VALUES ('53', '627test@qq.com', '627test@qq.com', 'l6jwevV/frgLi4O9Mx0D8g==', 'true', 'true', 'true', 'true', '2017-10-08 10:04:57', '2017-10-18 13:34:21', '627test@qq.com');
INSERT INTO `se_user` VALUES ('54', '1120@qq.com', '1120@qq.com', 'To4DhOx5/NqoLpl68J1VYg==', 'true', 'true', 'true', 'false', '2017-10-16 13:09:03', '2017-10-21 07:03:52', '1120@qq.com');
INSERT INTO `se_user` VALUES ('55', '234@qq.com', '234@qq.com', '8PkDBP+ImiSVMf2kC2SIcQ==', 'true', 'true', 'true', 'true', '2017-10-16 14:03:53', '2017-10-18 06:02:29', '234@qq.com');
INSERT INTO `se_user` VALUES ('64', '123test@qq.com', '123test@qq.com', '3vd6RZlGhcWfeP2amZ7VxA==', 'true', 'true', 'true', 'true', '2017-10-19 10:23:45', null, '123test@qq.com');
INSERT INTO `se_user` VALUES ('65', '1234test@qq.com', '1234test@qq.com', 'CsRmGZA6/fH/YaD94+yq/Q==', 'true', 'true', 'true', 'true', '2017-10-19 10:23:45', null, '1234test@qq.com');
INSERT INTO `se_user` VALUES ('73', '1234@qq.com', '1234@qq.com', 'YvNS5g2WRzqj+HgQdTYS+Q==', 'true', 'true', 'true', 'true', '2017-11-07 11:01:17', null, '1234@qq.com');

-- ----------------------------
-- Table structure for `se_user_info`
-- ----------------------------
DROP TABLE IF EXISTS `se_user_info`;
CREATE TABLE `se_user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '表主键',
  `user_id` int(11) NOT NULL COMMENT '对应用户主键',
  `user_head_id` varchar(128) NOT NULL DEFAULT 'default.jpg' COMMENT '用户头像名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of se_user_info
-- ----------------------------
INSERT INTO `se_user_info` VALUES ('1', '31', '08620572-b4af-4019-a4a3-68e941b92512.jpg');
INSERT INTO `se_user_info` VALUES ('2', '39', '028f439b-0b96-497f-bac9-8ba2cd44c6c5.jpg');
INSERT INTO `se_user_info` VALUES ('3', '43', 'default.jpg');
INSERT INTO `se_user_info` VALUES ('4', '44', 'default.jpg');
INSERT INTO `se_user_info` VALUES ('5', '45', 'default.jpg');
INSERT INTO `se_user_info` VALUES ('8', '53', 'default.jpg');
INSERT INTO `se_user_info` VALUES ('9', '54', 'default.jpg');
INSERT INTO `se_user_info` VALUES ('10', '55', 'default.jpg');
INSERT INTO `se_user_info` VALUES ('11', '59', 'default.jpg');
INSERT INTO `se_user_info` VALUES ('12', '66', 'default.jpg');
INSERT INTO `se_user_info` VALUES ('13', '67', 'default.jpg');
INSERT INTO `se_user_info` VALUES ('14', '68', 'default.jpg');
INSERT INTO `se_user_info` VALUES ('15', '69', 'default.jpg');
INSERT INTO `se_user_info` VALUES ('16', '70', 'default.jpg');
INSERT INTO `se_user_info` VALUES ('17', '71', 'default.jpg');
INSERT INTO `se_user_info` VALUES ('18', '72', 'default.jpg');

-- ----------------------------
-- Table structure for `se_user_role`
-- ----------------------------
DROP TABLE IF EXISTS `se_user_role`;
CREATE TABLE `se_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '表id',
  `user_id` int(11) NOT NULL COMMENT '用户主键',
  `role_id` int(11) NOT NULL COMMENT '角色主键',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of se_user_role
-- ----------------------------
INSERT INTO `se_user_role` VALUES ('11', '43', '2');
INSERT INTO `se_user_role` VALUES ('19', '31', '2');
INSERT INTO `se_user_role` VALUES ('25', '44', '2');
INSERT INTO `se_user_role` VALUES ('26', '45', '2');
INSERT INTO `se_user_role` VALUES ('27', '53', '2');
INSERT INTO `se_user_role` VALUES ('45', '54', '2');
INSERT INTO `se_user_role` VALUES ('51', '54', '1');
INSERT INTO `se_user_role` VALUES ('53', '39', '2');
INSERT INTO `se_user_role` VALUES ('54', '39', '1');
INSERT INTO `se_user_role` VALUES ('62', '72', '2');
