-- 用户搜索历史表
CREATE TABLE `user_search_history` (
  `history_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '历史记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `keyword` varchar(100) NOT NULL COMMENT '搜索关键词',
  `search_count` int(11) DEFAULT '1' COMMENT '搜索次数',
  `last_search_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后搜索时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`history_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_keyword` (`keyword`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户搜索历史表';
