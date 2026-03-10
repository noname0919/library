-- 仪表盘菜单新增配置
-- 父菜单ID: 2070

-- 1. 借阅趋势
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2072, '借阅趋势', 2070, 2, 'trend', 'dashboard/trend/index', 0, 0, 'C', '0', '0', 'library:dashboard:trend:view', '#', 'admin', NOW(), '', NOW(), '');

-- 2. 排行榜
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2073, '排行榜', 2070, 3, 'ranking', 'dashboard/ranking/index', 0, 0, 'C', '0', '0', 'library:dashboard:ranking:view', '#', 'admin', NOW(), '', NOW(), '');

-- 3. 实时动态
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2074, '实时动态', 2070, 4, 'realtime', 'dashboard/realtime/index', 0, 0, 'C', '0', '0', 'library:dashboard:realtime:view', '#', 'admin', NOW(), '', NOW(), '');

-- 4. 预警信息
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2075, '预警信息', 2070, 5, 'warning', 'dashboard/warning/index', 0, 0, 'C', '0', '0', 'library:dashboard:warning:view', '#', 'admin', NOW(), '', NOW(), '');
