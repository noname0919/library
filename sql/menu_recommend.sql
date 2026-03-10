-- 推荐图书菜单权限配置
-- 父菜单ID: 2064

-- 更新父菜单的component字段和is_frame字段
UPDATE sys_menu SET component = 'library/recommend/index', is_frame = 0 WHERE menu_id = 2064;

-- 1. 查询推荐图书列表
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2065, '查询推荐图书', 2064, 1, '', '', 0, 0, 'F', '0', '0', 'library:recommend:list', '#', 'admin', NOW(), '', NOW(), '');

-- 2. 随机推荐
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2066, '随机推荐', 2064, 2, '', '', 0, 0, 'F', '0', '0', 'library:recommend:random', '#', 'admin', NOW(), '', NOW(), '');

-- 3. 热门推荐
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2067, '热门推荐', 2064, 3, '', '', 0, 0, 'F', '0', '0', 'library:recommend:hot', '#', 'admin', NOW(), '', NOW(), '');

-- 4. 关键词推荐
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2068, '关键词推荐', 2064, 4, '', '', 0, 0, 'F', '0', '0', 'library:recommend:keyword', '#', 'admin', NOW(), '', NOW(), '');
