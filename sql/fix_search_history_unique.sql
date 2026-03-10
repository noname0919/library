-- 为用户搜索历史表添加唯一索引
-- 这样同一个用户搜索同一个关键词时，会更新记录而不是插入新记录

-- 先删除重复数据（保留search_count最大的那条）
DELETE h1 FROM user_search_history h1
INNER JOIN user_search_history h2 
WHERE h1.history_id < h2.history_id 
  AND h1.user_id = h2.user_id 
  AND h1.keyword = h2.keyword;

-- 添加唯一索引
ALTER TABLE user_search_history ADD UNIQUE KEY `uk_user_keyword` (`user_id`, `keyword`);
