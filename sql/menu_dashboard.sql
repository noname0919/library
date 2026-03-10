-- 仪表盘菜单权限配置
-- 父菜单ID: 2070

-- 1. 更新父菜单的component字段和is_frame字段
UPDATE sys_menu SET 
    component = 'dashboard/index', 
    is_frame = 0,
    menu_type = 'C',
    path = 'index'
WHERE menu_id = 2070;

-- 2. 查询仪表盘数据权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (2071, '查询仪表盘数据', 2070, 1, '', '', 0, 0, 'F', '0', '0', 'library:dashboard:view', '#', 'admin', NOW(), '', NOW(), '');
