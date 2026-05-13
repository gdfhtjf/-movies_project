-- ==========================================
-- 在线电影购票系统 - Spring Boot版数据库迁移
-- 在原movie_db基础上新增字段
-- ==========================================

USE movie_db;

-- 电影表新增字段：主演、上映日期、时长、类型、创建时间、更新时间
ALTER TABLE movies
    ADD COLUMN IF NOT EXISTS cast VARCHAR(255) COMMENT '主演',
    ADD COLUMN IF NOT EXISTS release_date DATE COMMENT '上映日期',
    ADD COLUMN IF NOT EXISTS duration INT COMMENT '影片时长（分钟）',
    ADD COLUMN IF NOT EXISTS genre VARCHAR(100) COMMENT '影片类型',
    ADD COLUMN IF NOT EXISTS create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    ADD COLUMN IF NOT EXISTS update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- 用户表新增字段：创建时间
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
