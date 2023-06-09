/*
 Navicat Premium Data Transfer

 Source Server         : localhost_mysql8.0.31
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : localhost:3306
 Source Schema         : binapi

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 23/05/2023 02:20:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_interface_info
-- ----------------------------
DROP TABLE IF EXISTS `tb_interface_info`;
CREATE TABLE `tb_interface_info`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `method` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'GET' COMMENT '请求类型',
  `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接口地址',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数',
  `request_header` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求头',
  `response_header` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '响应头',
  `price` decimal(10, 2) UNSIGNED NOT NULL DEFAULT 0.01 COMMENT '计费规则(元/条)',
  `status` int NOT NULL DEFAULT 1 COMMENT '接口状态（0-关闭，1-开启）',
  `left_num` int UNSIGNED NOT NULL COMMENT '接口剩余能够被调用的次数',
  `total_num` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '接口已经被调用的总次数，初始默认是0',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '创建人ID',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '接口信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_interface_info
-- ----------------------------
INSERT INTO `tb_interface_info` VALUES (12, '那永远的梦想', '我在哪里，我的梦想就在哪里', 'GET', 'http://localhost:8888/interfaces/name/get', NULL, '\'Accept-Encoding\': \'gzip, deflate, br\'', '\'Connection\': \'keep-alive\'', 0.01, 1, 800, 3, 1378088041316355, '2023-05-19 14:44:56', '2023-05-21 22:02:19', 0);
INSERT INTO `tb_interface_info` VALUES (1380170301538304, '一言文本api接口', '请求获得一个来自网络的句子，并以纯文本格式输出', 'GET', 'http://localhost:8888/interfaces/hitokoto/get', NULL, '\'Accept-Encoding\': \'gzip, deflate, br\'', '\'Connection\': \'keep-alive\'', 0.01, 1, 950, 0, 1380078046044160, '2023-05-23 01:56:40', '2023-05-23 02:02:19', 0);

-- ----------------------------
-- Table structure for tb_soul_soup
-- ----------------------------
DROP TABLE IF EXISTS `tb_soul_soup`;
CREATE TABLE `tb_soul_soup`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `content` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '鸡汤内容',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除，0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '心灵鸡汤表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_soul_soup
-- ----------------------------

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `account` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
  `phone` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png' COMMENT '用户头像',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别',
  `role` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'user' COMMENT '用户角色：user / admin',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `access_key` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'accessKey',
  `secret_key` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'secretKey',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uni_userAccount`(`account`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1380078046044161 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES (1378088041316353, NULL, 'zyshu', NULL, NULL, 'https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png', NULL, 'user', 'f4c9beb30393bb3d3d1fc103e37f79e3', '21dde9ef2bf4cb0b3fd3376cf72dc936', '580f6cb9a0d42356ccb955a232bf98bd', '2023-05-19 11:33:10', '2023-05-19 11:33:10', 0);
INSERT INTO `tb_user` VALUES (1378088041316354, NULL, 'Carl', NULL, NULL, 'https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png', NULL, 'user', 'f4c9beb30393bb3d3d1fc103e37f79e3', '2cbf1d1a5d039c1717945bc255b57a69', 'd485880b9ece2f54cc3f41b0081895a7', '2023-05-19 11:45:40', '2023-05-19 11:45:40', 0);
INSERT INTO `tb_user` VALUES (1378088041316355, NULL, 'admin', NULL, NULL, 'https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png', NULL, 'admin', 'f4c9beb30393bb3d3d1fc103e37f79e3', '63074d5d3ffe0fc40d9581b021312047', '5a0db3f8b6b2a5924939608247ecc929', '2023-05-19 14:35:47', '2023-05-19 14:36:03', 0);
INSERT INTO `tb_user` VALUES (1380078046044160, NULL, 'Margi', NULL, NULL, 'https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png', NULL, 'user', 'f4c9beb30393bb3d3d1fc103e37f79e3', '13f4ee3f98b43b28dbae065344e01ce5', 'd4726ca2baca631d05cff570a801b128', '2023-05-21 18:39:43', '2023-05-21 18:39:43', 0);

-- ----------------------------
-- Table structure for tb_user_interface_info
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_interface_info`;
CREATE TABLE `tb_user_interface_info`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '调用用户 id',
  `interface_info_id` bigint UNSIGNED NOT NULL COMMENT '接口 id',
  `left_num` int UNSIGNED NOT NULL COMMENT '每个用户操作该接口还剩余的调用次数',
  `total_num` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '每个用户操作该接口的总调用次数',
  `status` int NOT NULL DEFAULT 1 COMMENT '0-禁用，1-正常',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户调用接口信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_user_interface_info
-- ----------------------------
INSERT INTO `tb_user_interface_info` VALUES (1, 1378088041316355, 12, 197, 3, 1, '2023-05-21 19:59:07', '2023-05-21 22:01:39', 0);
INSERT INTO `tb_user_interface_info` VALUES (1380170579763200, 1380078046044160, 1380170301538304, 48, 2, 1, '2023-05-23 02:02:19', '2023-05-23 02:18:38', 0);

SET FOREIGN_KEY_CHECKS = 1;
