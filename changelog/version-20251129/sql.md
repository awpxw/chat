/*
Navicat Premium Dump SQL

Source Server         : vm-mysql
Source Server Type    : MySQL
Source Server Version : 80044 (8.0.44)
Source Host           : 192.168.209.131:3306
Source Schema         : chat

Target Server Type    : MySQL
Target Server Version : 80044 (8.0.44)
File Encoding         : 65001

Date: 04/12/2025 14:21:21
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_banned_user
-- ----------------------------
DROP TABLE IF EXISTS `t_banned_user`;
CREATE TABLE `t_banned_user`  (
`id` bigint NOT NULL AUTO_INCREMENT,
`user_id` bigint NOT NULL,
`type` tinyint NULL DEFAULT NULL COMMENT '1禁言 2封号',
`reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`end_time` datetime NULL DEFAULT NULL,
`operator_id` bigint NULL DEFAULT NULL,
`create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
`update_time` datetime NULL DEFAULT NULL,
`deleted` tinyint NULL DEFAULT NULL,
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '禁言封号记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_conversation
-- ----------------------------
DROP TABLE IF EXISTS `t_conversation`;
CREATE TABLE `t_conversation`  (
`id` bigint NOT NULL AUTO_INCREMENT,
`type` tinyint NOT NULL COMMENT '1单聊 2群聊 3客服会话',
`name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`creator_id` bigint NULL DEFAULT NULL,
`member_count` int NULL DEFAULT 2,
`is_pinned` tinyint NULL DEFAULT 0,
`mute` tinyint NULL DEFAULT 0,
`created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
`updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`id`) USING BTREE,
INDEX `idx_type_created`(`type` ASC, `created_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会话表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_conversation_member
-- ----------------------------
DROP TABLE IF EXISTS `t_conversation_member`;
CREATE TABLE `t_conversation_member`  (
`conversation_id` bigint NOT NULL,
`user_id` bigint NOT NULL,
`role` tinyint NULL DEFAULT 0 COMMENT '0普通 1管理员 2隐身人',
`unread_count` int NULL DEFAULT 0,
`last_read_msg_id` bigint NULL DEFAULT NULL,
`last_read_time` datetime NULL DEFAULT NULL,
`joined_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`conversation_id`, `user_id`) USING BTREE,
INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会话成员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_customer_session
-- ----------------------------
DROP TABLE IF EXISTS `t_customer_session`;
CREATE TABLE `t_customer_session`  (
`conversation_id` bigint NOT NULL,
`visitor_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
`status` tinyint NULL DEFAULT 0 COMMENT '0等待接入 1服务中 2已转接 3已结束',
`assign_user_id` bigint NULL DEFAULT NULL,
`queue_time` datetime NULL DEFAULT NULL,
`first_response_time` datetime NULL DEFAULT NULL,
`end_time` datetime NULL DEFAULT NULL,
`satisfaction_score` tinyint NULL DEFAULT NULL,
`satisfaction_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
PRIMARY KEY (`conversation_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客服会话扩展表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_customer_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_customer_tag`;
CREATE TABLE `t_customer_tag`  (
`id` bigint NOT NULL AUTO_INCREMENT,
`name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
`color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客户标签' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_customer_tag_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_customer_tag_relation`;
CREATE TABLE `t_customer_tag_relation`  (
`visitor_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
`tag_id` bigint NOT NULL,
PRIMARY KEY (`visitor_id`, `tag_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客户标签关联' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dept
-- ----------------------------
DROP TABLE IF EXISTS `t_dept`;
CREATE TABLE `t_dept`  (
`id` bigint NOT NULL AUTO_INCREMENT,
`parent_id` bigint NULL DEFAULT 0 COMMENT '父部门ID',
`name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
`sort` int NULL DEFAULT 0,
`status` tinyint NULL DEFAULT 1 COMMENT '1启用 0禁用',
`created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
`updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`id`) USING BTREE,
INDEX `idx_parent`(`parent_id` ASC) USING BTREE,
INDEX `idx_status_sort_parent`(`status` ASC, `sort` ASC, `parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_emoji
-- ----------------------------
DROP TABLE IF EXISTS `t_emoji`;
CREATE TABLE `t_emoji`  (
`id` bigint NOT NULL AUTO_INCREMENT,
`code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
`url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
`category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`sort` int NULL DEFAULT 0,
PRIMARY KEY (`id`) USING BTREE,
UNIQUE INDEX `code`(`code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '表情包' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_file_record
-- ----------------------------
DROP TABLE IF EXISTS `t_file_record`;
CREATE TABLE `t_file_record`  (
`id` bigint NOT NULL AUTO_INCREMENT,
`origin_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
`object_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
`url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
`size` bigint NULL DEFAULT NULL,
`mime_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`uploader_id` bigint NULL DEFAULT NULL,
`upload_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
`expire_time` datetime NULL DEFAULT NULL,
PRIMARY KEY (`id`) USING BTREE,
INDEX `idx_uploader`(`uploader_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件上传记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_login_log
-- ----------------------------
DROP TABLE IF EXISTS `t_login_log`;
CREATE TABLE `t_login_log`  (
`id` bigint NOT NULL AUTO_INCREMENT,
`user_id` bigint NULL DEFAULT NULL,
`username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`device` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`status` tinyint NULL DEFAULT 1 COMMENT '1成功 0失败',
`msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`login_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '登录日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_menu`;
CREATE TABLE `t_menu`  (
`id` bigint NOT NULL AUTO_INCREMENT,
`parent_id` bigint NULL DEFAULT 0,
`name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
`path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`component` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`type` tinyint NOT NULL COMMENT '0目录 1菜单 2按钮',
`icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`sort` int NULL DEFAULT 0,
`visible` tinyint NULL DEFAULT 1,
`created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_message
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message`  (
`id` bigint NOT NULL AUTO_INCREMENT,
`conversation_id` bigint NOT NULL,
`sender_id` bigint NOT NULL,
`msg_type` tinyint NULL DEFAULT 1 COMMENT '1文本 2图片 3文件 4语音 5视频 6撤回 7系统消息 8已读回执',
`content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
`extra` json NULL,
`msg_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
`is_deleted` tinyint NULL DEFAULT 0,
PRIMARY KEY (`id`) USING BTREE,
INDEX `idx_conversation_time`(`conversation_id` ASC, `msg_time` DESC) USING BTREE,
INDEX `idx_sender_time`(`sender_id` ASC, `msg_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息表（热存储）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_message_extra
-- ----------------------------
DROP TABLE IF EXISTS `t_message_extra`;
CREATE TABLE `t_message_extra`  (
`msg_id` bigint NOT NULL,
`file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`file_size` bigint NULL DEFAULT NULL,
`duration` int NULL DEFAULT NULL COMMENT '语音/视频时长秒',
`width` int NULL DEFAULT NULL,
`height` int NULL DEFAULT NULL,
PRIMARY KEY (`msg_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息扩展信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_message_read
-- ----------------------------
DROP TABLE IF EXISTS `t_message_read`;
CREATE TABLE `t_message_read`  (
`msg_id` bigint NOT NULL,
`user_id` bigint NOT NULL,
`read_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`msg_id`, `user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息已读回执表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_message_revoke
-- ----------------------------
DROP TABLE IF EXISTS `t_message_revoke`;
CREATE TABLE `t_message_revoke`  (
`msg_id` bigint NOT NULL,
`operator_id` bigint NOT NULL,
`reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`revoke_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`msg_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息撤回记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `t_operation_log`;
CREATE TABLE `t_operation_log`  (
`id` bigint NOT NULL AUTO_INCREMENT,
`user_id` bigint NULL DEFAULT NULL,
`username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
`time` bigint NULL DEFAULT NULL,
`ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_quick_reply
-- ----------------------------
DROP TABLE IF EXISTS `t_quick_reply`;
CREATE TABLE `t_quick_reply`  (
`id` bigint NOT NULL AUTO_INCREMENT,
`content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
`category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`user_id` bigint NULL DEFAULT 0 COMMENT '0为公共',
`sort` int NULL DEFAULT 0,
`status` tinyint NULL DEFAULT 1,
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '快捷回复' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role`  (
`id` bigint NOT NULL AUTO_INCREMENT,
`name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
`code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`data_scope` tinyint NULL DEFAULT 4 COMMENT '1全部 2本级及子级 3本级 4仅本人 5自定义',
`status` tinyint NULL DEFAULT 1,
`remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`id`) USING BTREE,
UNIQUE INDEX `code`(`code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_role_menu`;
CREATE TABLE `t_role_menu`  (
`role_id` bigint NOT NULL,
`menu_id` bigint NOT NULL,
PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色菜单关联' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sensitive_word
-- ----------------------------
DROP TABLE IF EXISTS `t_sensitive_word`;
CREATE TABLE `t_sensitive_word`  (
`id` bigint NOT NULL AUTO_INCREMENT,
`word` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
`level` tinyint NULL DEFAULT 1 COMMENT '1轻微 2严重',
`status` tinyint NULL DEFAULT 1,
PRIMARY KEY (`id`) USING BTREE,
UNIQUE INDEX `word`(`word` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '敏感词库' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_system_config
-- ----------------------------
DROP TABLE IF EXISTS `t_system_config`;
CREATE TABLE `t_system_config`  (
`config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
`config_value` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`config_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
`id` bigint NOT NULL AUTO_INCREMENT,
`work_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工号',
`name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
`nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`mobile` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`dept_id` bigint NULL DEFAULT NULL,
`position` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`status` tinyint NULL DEFAULT 1 COMMENT '1在职 2离职 0禁用',
`is_admin` tinyint NULL DEFAULT 0,
`created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
`update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
`password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
`create_time` datetime NULL DEFAULT NULL,
`deleted` tinyint NULL DEFAULT NULL,
PRIMARY KEY (`id`) USING BTREE,
UNIQUE INDEX `work_no`(`work_no` ASC) USING BTREE,
INDEX `idx_dept`(`dept_id` ASC) USING BTREE,
INDEX `idx_mobile`(`mobile` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1996148884936118275 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '员工表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_role
-- ----------------------------
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role`  (
`user_id` bigint NOT NULL,
`role_id` bigint NOT NULL,
PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_visitor
-- ----------------------------
DROP TABLE IF EXISTS `t_visitor`;
CREATE TABLE `t_visitor`  (
`id` bigint NOT NULL AUTO_INCREMENT,
`visitor_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
`ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
`source` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来源页面',
`city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
`first_visit_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
`last_visit_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`id`) USING BTREE,
UNIQUE INDEX `visitor_id`(`visitor_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '网站访客表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
