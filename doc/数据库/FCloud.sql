/*
 Navicat Premium Dump SQL

 Source Server         : mysql.ccxc.vip
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : mysql.ccxc.vip:3306
 Source Schema         : FCloud

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 30/05/2026 00:37:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for xc_disk
-- ----------------------------
DROP TABLE IF EXISTS `xc_disk`;
CREATE TABLE `xc_disk`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '磁盘名称',
  `disk_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '磁盘编号',
  `disk_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '磁盘秘钥',
  `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户主键',
  `group_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '磁盘组主键',
  `cloud_space` bigint NOT NULL DEFAULT 0 COMMENT '网盘空间',
  `free_flow` bigint NOT NULL DEFAULT 0 COMMENT '下载流量',
  `share_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '共享code',
  `sign_valid_time` bigint NOT NULL COMMENT '签名有效时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 不能为空',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 不能为空',
  `version` int NOT NULL DEFAULT 0 COMMENT '乐观锁 不能为空',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `share_code`(`share_code` ASC) USING BTREE COMMENT 'share_code唯一索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for xc_file
-- ----------------------------
DROP TABLE IF EXISTS `xc_file`;
CREATE TABLE `xc_file`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名称',
  `size` bigint NOT NULL DEFAULT 0 COMMENT '文件大小',
  `folder_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件夹主键',
  `disk_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '磁盘主键',
  `hash_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '哈希主键',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '状态：0：有效，1：无效',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 不能为空',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 不能为空',
  `version` int NOT NULL DEFAULT 0 COMMENT '乐观锁 不能为空',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name_folder_user`(`name` ASC, `folder_id` ASC, `disk_id` ASC) USING BTREE,
  INDEX `folder_id`(`folder_id` ASC) USING BTREE COMMENT '查询索引',
  INDEX `user_id`(`disk_id` ASC) USING BTREE COMMENT '查询索引',
  INDEX `hash_id`(`hash_id` ASC) USING BTREE COMMENT '查询索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for xc_file_hash
-- ----------------------------
DROP TABLE IF EXISTS `xc_file_hash`;
CREATE TABLE `xc_file_hash`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标识，对应真实文件名称',
  `hash_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'hash值',
  `size` bigint NOT NULL DEFAULT 0 COMMENT '文件大小',
  `server_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '服务器地址',
  `group_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组标识',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE COMMENT 'code唯一索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for xc_folder
-- ----------------------------
DROP TABLE IF EXISTS `xc_folder`;
CREATE TABLE `xc_folder`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件夹名称',
  `node` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '节点',
  `parent_node` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父节点',
  `disk_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '磁盘主键',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 不能为空',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 不能为空',
  `version` int NOT NULL DEFAULT 0 COMMENT '乐观锁 不能为空',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name_parent_user`(`name` ASC, `parent_node` ASC, `disk_id` ASC) USING BTREE COMMENT '名称目录用户必须唯一',
  INDEX `parent_node`(`parent_node` ASC) USING BTREE COMMENT '查询索引',
  INDEX `user_id`(`disk_id` ASC) USING BTREE COMMENT '查询索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for xc_safety_chain
-- ----------------------------
DROP TABLE IF EXISTS `xc_safety_chain`;
CREATE TABLE `xc_safety_chain`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '规则名称',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件夹路径',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '链接规则',
  `verify_sign` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '验证签名',
  `allow_suffix` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '允许访问的后缀',
  `disk_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '磁盘主键',
  `status` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '区域状态，对应字典表的effectStatus，0：有效，1：无效',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 不能为空',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 不能为空',
  `version` int NOT NULL DEFAULT 0 COMMENT '版本，乐观锁',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `disk_id__path__url`(`disk_id` ASC, `path` ASC, `url` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for xc_share
-- ----------------------------
DROP TABLE IF EXISTS `xc_share`;
CREATE TABLE `xc_share`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '共享名称',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '共享文件代码',
  `valid_time` bigint NOT NULL COMMENT '有效期：0：永久有效',
  `draw_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '提取码',
  `disk_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '磁盘主键',
  `preserve_num` bigint NOT NULL DEFAULT 0 COMMENT '保存次数',
  `browse_num` bigint NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `download_num` bigint NOT NULL DEFAULT 0 COMMENT '下载次数',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '共享备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 不能为空',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 不能为空',
  `version` int NOT NULL DEFAULT 0 COMMENT '乐观锁 不能为空',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for xc_share_folder_file
-- ----------------------------
DROP TABLE IF EXISTS `xc_share_folder_file`;
CREATE TABLE `xc_share_folder_file`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `share_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '共享id',
  `file_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件id',
  `folder_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件夹id',
  `is_root` int NULL DEFAULT NULL COMMENT '是否是根，0：是，1：不是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `share_file`(`share_id` ASC, `file_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for xc_statistics
-- ----------------------------
DROP TABLE IF EXISTS `xc_statistics`;
CREATE TABLE `xc_statistics`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型，0：开始，1：异常，2：结束',
  `statistics_time` datetime NULL DEFAULT NULL COMMENT '统计时间',
  `client_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端ip地址',
  `method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求方法',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户访问url',
  `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户主键',
  `param_json` json NULL COMMENT '请求参数',
  `response_time` decimal(10, 3) NULL DEFAULT NULL COMMENT '响应时长，单位秒',
  `result_json` json NULL COMMENT '返回参数',
  `error_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '异常类型，0：操作异常，1：系统异常',
  `error_message` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '异常消息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_client_ip`(`client_ip` ASC) USING BTREE COMMENT '客户端ip索引',
  INDEX `index_statistics_time`(`statistics_time` ASC) USING BTREE COMMENT '统计时间索引',
  INDEX `index_type`(`type` ASC) USING BTREE COMMENT '类型索引',
  INDEX `index_error_type`(`error_type` ASC) USING BTREE COMMENT '错误类型索引',
  INDEX `index_url`(`url` ASC) USING BTREE COMMENT 'url地址索引',
  INDEX `index_user_id`(`user_id` ASC) USING BTREE COMMENT '用户主键索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for xc_statistics_2026_05
-- ----------------------------
DROP TABLE IF EXISTS `xc_statistics_2026_05`;
CREATE TABLE `xc_statistics_2026_05`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型，0：开始，1：异常，2：结束',
  `statistics_time` datetime NULL DEFAULT NULL COMMENT '统计时间',
  `client_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端ip地址',
  `method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求方法',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户访问url',
  `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户主键',
  `param_json` json NULL COMMENT '请求参数',
  `response_time` decimal(10, 3) NULL DEFAULT NULL COMMENT '响应时长，单位秒',
  `result_json` json NULL COMMENT '返回参数',
  `error_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '异常类型，0：操作异常，1：系统异常',
  `error_message` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '异常消息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_client_ip`(`client_ip` ASC) USING BTREE COMMENT '客户端ip索引',
  INDEX `index_statistics_time`(`statistics_time` ASC) USING BTREE COMMENT '统计时间索引',
  INDEX `index_type`(`type` ASC) USING BTREE COMMENT '类型索引',
  INDEX `index_error_type`(`error_type` ASC) USING BTREE COMMENT '错误类型索引',
  INDEX `index_url`(`url` ASC) USING BTREE COMMENT 'url地址索引',
  INDEX `index_user_id`(`user_id` ASC) USING BTREE COMMENT '用户主键索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
