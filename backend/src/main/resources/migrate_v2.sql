-- ==========================================
-- V2 Migration: order status, optimistic lock, cancel time
-- MySQL 5.7+ compatible
-- ==========================================

USE movie_db;

DROP PROCEDURE IF EXISTS add_column_if_not_exists;

DELIMITER $$

CREATE PROCEDURE add_column_if_not_exists(
    IN tbl_name VARCHAR(64),
    IN col_name VARCHAR(64),
    IN col_def VARCHAR(500)
)
BEGIN
    DECLARE col_count INT;
    SELECT COUNT(*) INTO col_count
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = tbl_name
      AND COLUMN_NAME = col_name;
    IF col_count = 0 THEN
        SET @sql = CONCAT('ALTER TABLE ', tbl_name, ' ADD COLUMN ', col_name, ' ', col_def);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DELIMITER ;

CALL add_column_if_not_exists('orders', 'status',
    "VARCHAR(20) DEFAULT 'paid'");

CALL add_column_if_not_exists('orders', 'version',
    "INT DEFAULT 1");

CALL add_column_if_not_exists('orders', 'cancel_time',
    "DATETIME");

CALL add_column_if_not_exists('screenings', 'version',
    "INT DEFAULT 1");

DROP PROCEDURE IF EXISTS add_column_if_not_exists;

SET @index_exists = (
    SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'orders'
      AND INDEX_NAME = 'idx_screening_status'
);

SET @sql = IF(@index_exists = 0,
    'ALTER TABLE orders ADD INDEX idx_screening_status (screening_id, status)',
    "SELECT 'index already exists' AS msg"
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
