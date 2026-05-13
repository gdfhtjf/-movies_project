CREATE TABLE IF NOT EXISTS hall_layouts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    hall_number VARCHAR(20) NOT NULL UNIQUE,
    rows_num INT NOT NULL DEFAULT 8,
    cols_num INT NOT NULL DEFAULT 12,
    layout_json TEXT COMMENT 'JSON: {aisles:[], exits:[], blocked:[]}',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 预置7个影厅默认布局
INSERT IGNORE INTO hall_layouts (hall_number, rows_num, cols_num) VALUES
('1号厅', 8, 12), ('2号厅', 8, 12), ('3号厅', 8, 12),
('4号厅', 6, 10), ('5号厅', 6, 10),
('IMAX厅', 12, 18), ('VIP厅', 5, 8);
