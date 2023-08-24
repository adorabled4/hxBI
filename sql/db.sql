/*
 Navicat Premium Data Transfer

 Source Server         : centsos-192.168.159.134
 Source Server Type    : MySQL
 Source Server Version : 50740
 Source Host           : 192.168.159.134:3306
 Source Schema         : bi

 Target Server Type    : MySQL
 Target Server Version : 50740
 File Encoding         : 65001

 Date: 24/08/2023 13:59:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_chart
-- ----------------------------
DROP TABLE IF EXISTS `t_chart`;
CREATE TABLE `t_chart`  (
                            `id` bigint(20) NOT NULL COMMENT 'id',
                            `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
                            `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表名称',
                            `goal` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '分析目标',
                            `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'succeed' COMMENT 'wait,running,succeed,failed',
                            `exec_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行信息',
                            `chart_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '图表数据',
                            `chart_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图表类型',
                            `gen_chart` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '生成的图表数据',
                            `gen_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '生成的分析结论',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `is_delete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '图表表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
                           `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                           `user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '昵称',
                           `user_account` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
                           `avatar_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'https://blog.dhx.icu/img/avater.png' COMMENT '头像',
                           `gender` tinyint(4) NOT NULL DEFAULT 1 COMMENT '性别(1男0女)',
                           `user_password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
                           `address` varchar(96) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地址',
                           `birth` datetime NULL DEFAULT NULL COMMENT '出生日期',
                           `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电话',
                           `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
                           `user_role` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0-用户 1-管理员',
                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           `is_delete` tinyint(4) NOT NULL DEFAULT 0 COMMENT '逻辑删除(1删除)',
                           PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
