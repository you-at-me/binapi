/*
 Navicat Premium Data Transfer

 Source Server         : localhost_mysql8.0.31
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : localhost:3306
 Source Schema         : open_api

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 17/05/2023 12:27:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for interface_info
-- ----------------------------
DROP TABLE IF EXISTS `interface_info`;
CREATE TABLE `interface_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '描述',
  `method` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求类型',
  `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接口地址',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数',
  `request_header` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求头',
  `response_header` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '响应头',
  `price` decimal(10, 2) NOT NULL COMMENT '计费规则(元/条)' default 0.02,
  `status` int NOT NULL DEFAULT 0 COMMENT '接口状态（0-关闭，1-开启）',
  `user_id` bigint NOT NULL COMMENT '创建人',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '接口信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of interface_info
-- ----------------------------

insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('许擎宇', '薛聪健', 'www.cary-king.net', '潘博涛', '谭聪健', 0, '石炫明', 9500534531);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('陆弘文', '白志强', 'www.leslee-kuhn.net', '潘懿轩', '马鸿涛', 0, '陈峻熙', 3982575846);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('毛建辉', '罗文', 'www.rosaria-kilback.io', '冯子默', '彭哲瀚', 0, '赵远航', 121776355);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('彭雨泽', '蔡煜祺', 'www.norris-bergstrom.biz', '董思源', '田晓博', 0, '潘擎宇', 740);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('傅志强', '陈梓晨', 'www.jordan-reinger.com', '金志强', '熊锦程', 0, '邓睿渊', 35542559);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('吕黎昕', '孔越彬', 'www.fe-okon.info', '万伟宸', '林昊然', 0, '孟荣轩', 1445);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('夏雪松', '许子骞', 'www.lashawna-legros.co', '蔡昊然', '胡鹏涛', 0, '钟立辉', 34075514);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('严钰轩', '阎志泽', 'www.kay-funk.biz', '莫皓轩', '郭黎昕', 0, '龚天宇', 70956);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('萧嘉懿', '曹熠彤', 'www.margarette-lindgren.biz', '田泽洋', '邓睿渊', 0, '梁志强', 98);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('杜驰', '冯思源', 'www.vashti-auer.org', '黎健柏', '武博文', 0, '李伟宸', 9);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('史金鑫', '蔡鹏涛', 'www.diann-keebler.org', '徐烨霖', '阎建辉', 0, '李烨伟', 125);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('林炫明', '贾旭尧', 'www.dotty-kuvalis.io', '梁雨泽', '龙伟泽', 0, '许智渊', 79998);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('何钰轩', '赖智宸', 'www.andy-adams.net', '崔思淼', '白鸿煊', 0, '邵振家', 7167482751);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('魏志强', '于立诚', 'www.ione-aufderhar.biz', '朱懿轩', '万智渊', 0, '唐昊强', 741098);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('严君浩', '金胤祥', 'www.duane-boyle.org', '雷昊焱', '侯思聪', 0, '郝思', 580514);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('姚皓轩', '金鹏', 'www.lyda-klein.biz', '杜昊强', '邵志泽', 0, '冯鸿涛', 6546);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('廖驰', '沈泽洋', 'www.consuelo-sipes.info', '彭昊然', '邓耀杰', 0, '周彬', 7761037);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('赖智渊', '邓志泽', 'www.emerson-mann.co', '熊明哲', '贺哲瀚', 0, '田鹏', 381422);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('许涛', '陆致远', 'www.vella-ankunding.name', '贾哲瀚', '莫昊焱', 0, '袁越彬', 4218096);
insert into `interface_info` (`name`, `description`, `url`, `request_header`, `response_header`, `status`, `method`, `user_id`) values ('吕峻熙', '沈鹏飞', 'www.shari-reichel.org', '郭鸿煊', '覃烨霖', 0, '熊黎昕', 493);


-- ----------------------------
-- Table structure for soul_soup
-- ----------------------------
DROP TABLE IF EXISTS `soul_soup`;
CREATE TABLE `soul_soup`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `content` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '鸡汤内容',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除，0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '心灵鸡汤表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of soul_soup
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `account` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
  `phone` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
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
) ENGINE = InnoDB AUTO_INCREMENT = 1378088041316353 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1378088041316352, NULL, 'admin', NULL, '2022248374@qq.com', 'https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png', NULL, 'admin', '194344925efb6fdd57eb0384376fa278', 'b569bd6b1ebb1c9f193b5739df444b7b', 'c2de412d6a0414b1686dd602195a2396', '2023-04-23 15:52:58', '2023-04-23 15:53:30', 0);

-- ----------------------------
-- Table structure for user_interface_info
-- ----------------------------
DROP TABLE IF EXISTS `user_interface_info`;
CREATE TABLE `user_interface_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '调用用户 id',
  `interface_info_id` bigint NOT NULL COMMENT '接口 id',
  `total_num` int NOT NULL DEFAULT 0 COMMENT '总调用次数',
  `left_num` int NOT NULL DEFAULT 0 COMMENT '剩余调用次数',
  `status` int NOT NULL DEFAULT 1 COMMENT '0-禁用，1-正常',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户调用接口关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_interface_info
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
