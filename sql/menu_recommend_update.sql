-- 更新推荐图书菜单配置
-- 父菜单ID: 2064

-- 更新父菜单（目录类型）
UPDATE sys_menu SET 
    component = 'Layout',
    is_frame = 0,
    path = '/library/recommend',
    menu_type = 'M'
WHERE menu_id = 2064;

-- 删除可能存在的旧子菜单数据
DELETE FROM sys_menu WHERE parent_id = 2064;

-- 插入子菜单（菜单类型）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES 
(2065, '图书推荐', 2064, 1, 'index', 'library/recommend/index', 0, 0, 'C', '0', '0', 'library:recommend:list', '#', 'admin', NOW(), '', NOW(), '');

-- 插入按钮权限（按钮类型）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES 
(2066, '随机推荐', 2065, 1, '', '', 0, 0, 'F', '0', '0', 'library:recommend:random', '#', 'admin', NOW(), '', NOW(), ''),
(2067, '热门推荐', 2065, 2, '', '', 0, 0, 'F', '0', '0', 'library:recommend:hot', '#', 'admin', NOW(), '', NOW(), ''),
(2068, '关键词推荐', 2065, 3, '', '', 0, 0, 'F', '0', '0', 'library:recommend:keyword', '#', 'admin', NOW(), '', NOW(), ''),
(2069, '综合推荐', 2065, 4, '', '', 0, 0, 'F', '0', '0', 'library:recommend:all', '#', 'admin', NOW(), '', NOW(), '');
