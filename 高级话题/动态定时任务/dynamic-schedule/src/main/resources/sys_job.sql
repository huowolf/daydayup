
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job` (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务id',
  `bean_name` varchar(100) NOT NULL COMMENT 'bean名称',
  `method_name` varchar(100) NOT NULL COMMENT '方法名称',
  `method_params` varchar(255) DEFAULT NULL COMMENT '方法参数',
  `cron` varchar(255) NOT NULL COMMENT 'cron表达式',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `job_status` tinyint(4) NOT NULL COMMENT '状态（1：正常 0：停止）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
