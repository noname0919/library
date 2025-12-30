
-- 2. 图书基本信息表
CREATE TABLE `book` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '图书ID',
  `isbn` varchar(20) DEFAULT NULL COMMENT 'ISBN号',
  `book_name` varchar(200) NOT NULL COMMENT '图书名称',
  `author_name` varchar(100) NOT NULL COMMENT '作者姓名',
  `publisher_name` varchar(100) NOT NULL COMMENT '出版社名称',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `publish_date` date DEFAULT NULL COMMENT '出版日期',
  `price` decimal(10,2) DEFAULT NULL COMMENT '图书价格',
  `image` varchar(255) DEFAULT NULL COMMENT '封面图片',
  `total_quantity` int(11) DEFAULT '0' COMMENT '总馆藏数量',
  `available_quantity` int(11) DEFAULT '0' COMMENT '可借数量',
  `borrowed_quantity` int(11) DEFAULT '0' COMMENT '已借数量',
  `status` char(1) DEFAULT '0' COMMENT '图书状态（0上架 1下架）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_isbn` (`isbn`),
  UNIQUE KEY `uk_book_name` (`book_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='图书基本信息表';









