-- 修复菜单 is_frame 字段
-- 将内部页面菜单的 is_frame 改为 1（表示内部路由，不需要http(s)前缀）

UPDATE sys_menu SET is_frame = 1 WHERE menu_id = 2071;
