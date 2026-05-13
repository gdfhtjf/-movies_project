CREATE TABLE IF NOT EXISTS operation_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    method VARCHAR(10),
    uri VARCHAR(255),
    operation_type VARCHAR(20),
    ip VARCHAR(50),
    args_count INT,
    elapsed_ms BIGINT,
    status VARCHAR(10),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
