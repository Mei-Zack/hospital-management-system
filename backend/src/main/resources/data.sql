-- 初始化管理员用户 (密码: admin123)
INSERT INTO users (id, username, password, real_name, role, status, created_at, updated_at)
VALUES (1, 'admin', '$10$X/hX6JZV9U91dHjRMJJuxe6.eSZvEz7bHcNREcIHvhHEpVnGNnKXi', '系统管理员', 'ADMIN', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE id=id; 