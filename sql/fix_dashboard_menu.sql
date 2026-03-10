-- 修复仪表盘菜单配置
-- 让仪表盘像推荐图书一样有正确的父子菜单结构

-- 1. 将 2070 改为目录类型（M），作为父菜单
UPDATE sys_menu SET 
    menu_name = '仪表盘',
    path = '/dashboard',
    component = 'Layout',
    menu_type = 'M',
    perms = '',
    icon = 'dashboard'
WHERE menu_id = 2070;

-- 2. 将 2071 改为菜单类型（C），作为子菜单
UPDATE sys_menu SET 
    menu_name = '数据概览',
    parent_id = 2070,
    order_num = 1,
    path = 'index',
    component = 'dashboard/index',
    menu_type = 'C',
    perms = 'library:dashboard:view',
    icon = '#'
WHERE menu_id = 2071;
