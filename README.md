# 在线电影购票系统

全栈电影购票平台，支持用户浏览电影、在线选座购票、订单管理，管理员后台可管理电影、场次、订单和海报。

## 技术栈

| 层 | 技术 |
|---|------|
| 后端 | Spring Boot 4.0.6 / Java 17 / MyBatis-Plus 3.5 / MySQL |
| 前端 | Vue 3.5 / Vite 6 / Naive UI / ECharts / Pinia |
| 安全 | BCrypt 密码加密 / Spring Session JDBC / 乐观锁防超卖 |

## 项目结构

```
movies_project/
├── backend/             # Spring Boot 后端 (端口 8080)
├── frontend/            # Vue 3 前端 (端口 5173)
├── 前端启动.bat          # 前端启动脚本
└── README.md
```

## 快速开始

**前置条件：** MySQL 8.0+，Node.js 18+，JDK 17+，Maven 3.8+

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS movie_db"

# 2. 启动后端 (Terminal 1)
cd backend
mvn spring-boot:run

# 3. 启动前端 (Terminal 2)
cd frontend
npm install
npm run dev
```

打开 `http://localhost:5173`，使用 Admin / 123456 登录管理后台。

## 数据库表

- `movies` — 电影信息（标题、导演、价格、海报等）
- `users` — 用户（角色：admin/user，密码 BCrypt 加密）
- `screenings` — 放映场次（关联电影、影厅、时间、座位数）
- `orders` — 订单（关联用户/电影/场次、座位号、状态）

## API 端点

| 前缀 | 说明 |
|------|------|
| `/api/movies` | 电影 CRUD + 海报/预告片上传 |
| `/api/auth` | 登录/注册/登出 |
| `/api/users` | 用户管理 |
| `/api/orders` | 订单创建/取消/查询 |
| `/api/screenings` | 场次管理 |
| `/api/admin` | 统计数据/营收趋势 |
| `/api/posters` | 海报管理 |
| `/api/files` | 文件下载 |
