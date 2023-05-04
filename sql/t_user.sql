/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : localhost:3306
 Source Schema         : common

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 04/05/2023 17:41:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `user_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `user_account` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账户',
  `user_password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码',
  `address` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `avatar_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'https://blog.dhx.icu/img/avaterLANYANGYANG.jpg' COMMENT '头像地址',
  `gender` tinyint NULL DEFAULT 1 COMMENT '性别1男0女',
  `phone` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `last_login_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后一次登录的IP',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `user_role` tinyint NOT NULL DEFAULT 0 COMMENT '是否是管理员',
  `birth` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '出生日期',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12348 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
