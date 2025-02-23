-- 创建 routes 表来存储路由配置信息
CREATE TABLE routes (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '路由ID',
    path VARCHAR(255) NOT NULL COMMENT '路由路径',
    component VARCHAR(255) NOT NULL COMMENT '组件路径',
    name VARCHAR(255) COMMENT '路由名称',
    icon VARCHAR(100) COMMENT '路由图标',
    redirect VARCHAR(255) COMMENT '重定向路径',
    parent_id INT COMMENT '上级路由ID',
    permission VARCHAR(255) COMMENT '权限标识',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='路由配置表';