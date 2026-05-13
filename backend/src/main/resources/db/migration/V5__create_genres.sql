CREATE TABLE IF NOT EXISTS genres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS movie_genre (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    genre_id INT NOT NULL
);

-- 预置类型
INSERT IGNORE INTO genres (name) VALUES
('动作'), ('喜剧'), ('剧情'), ('科幻'), ('爱情'),
('悬疑'), ('恐怖'), ('动画'), ('奇幻'), ('犯罪'),
('战争'), ('历史'), ('武侠'), ('纪录片'), ('音乐');
