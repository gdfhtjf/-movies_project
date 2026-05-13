# 在线电影购票系统

全栈电影购票平台，支持用户浏览电影、在线选座购票、评论评分、收藏，管理员后台可管理电影、场次、订单、用户、海报和操作日志。

## 技术栈

| 层 | 技术 |
|---|------|
| 后端 | Spring Boot 4.0.6 / Java 17 / MyBatis-Plus 3.5 / MySQL 8.0 |
| 前端 | Vue 3.5 / Vite 6 / Naive UI / ECharts / Pinia / DM Sans + Noto Sans SC |
| 安全 | BCrypt 密码加密 / Spring Session JDBC / 乐观锁防超卖 / XSS 过滤 |
| 工具 | yt-dlp 预告片下载 / Impeccable 前端设计系统 / Claude Code Agent 体系 |

## 项目结构

```
movies_project/
├── backend/                         # Spring Boot 后端 (端口 8080)
│   ├── src/main/java/com/movie/
│   │   ├── controller/              # 11 个控制器 (60+ API 端点)
│   │   ├── service/                 # 服务层 (接口 + 实现)
│   │   ├── mapper/                  # MyBatis-Plus 数据访问层
│   │   ├── entity/                  # 实体类 (11 个)
│   │   ├── filter/                  # 7 个 Servlet 过滤器
│   │   ├── interceptor/             # 2 个 Spring 拦截器
│   │   ├── aspect/                  # AOP 操作日志切面
│   │   └── config/                  # 配置类
│   └── src/main/resources/
│       ├── schema.sql               # DDL 自动执行脚本
│       └── db/migration/            # 数据库迁移脚本 (V1-V6)
├── frontend/                        # Vue 3 前端 (端口 5173)
│   └── src/
│       ├── views/user/              # 11 个用户端页面
│       ├── views/admin/             # 7 个管理端页面
│       ├── components/              # 通用 + 业务组件
│       ├── stores/                  # 6 个 Pinia 状态模块
│       ├── composables/             # 组合式函数
│       └── layouts/                 # 用户端 + 管理端布局
├── .claude/                         # Claude Code 配置
│   ├── agents/                      # 5 个 Agent 定义
│   └── skills/impeccable/           # Impeccable 前端设计 skill
└── 前端启动.bat                      # 前端启动脚本
```

## 快速开始

**前置条件：** MySQL 8.0+，Node.js 18+，JDK 17+，Maven 3.8+

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS movie_db"

# 2. 设置数据库密码环境变量
set DB_PASSWORD=你的MySQL密码

# 3. 启动后端 (Terminal 1)
cd backend
mvn spring-boot:run

# 4. 启动前端 (Terminal 2)
cd frontend
npm install
npm run dev
```

打开 `http://localhost:5173`，使用 `Admin` / `123456` 登录管理后台。

> **注意：** 数据库密码已改为环境变量 `${DB_PASSWORD}`，不再硬编码在 `application.yml` 中。启动后端前务必设置该环境变量。

## 数据库表

| 表 | 说明 |
|----|------|
| `movies` | 电影信息（标题、导演、演员、价格、海报、预告片等） |
| `users` | 用户（角色：admin/user，BCrypt 加密，支持邮箱/手机/头像） |
| `screenings` | 放映场次（关联电影/影厅，乐观锁防超卖） |
| `orders` | 订单（pending→paid→ticketed→completed 状态流转） |
| `favorites` | 用户收藏 |
| `reviews` | 电影评分与评论（1-5 星） |
| `genres` / `movie_genre` | 规范化电影类型（15 种预置） |
| `hall_layouts` | 影厅座位布局（7 个影厅预设） |
| `operation_logs` | 持久化操作日志 |

## API 端点

| 前缀 | 说明 |
|------|------|
| `/api/movies` | 电影 CRUD + 海报/预告片上传 |
| `/api/auth` | 登录/注册/登出/忘记密码/重置密码 |
| `/api/users` | 用户管理 + 资料编辑 |
| `/api/orders` | 订单创建/取消/支付/查询 |
| `/api/screenings` | 场次管理 + 时间冲突检测 |
| `/api/reviews` | 电影评分与评论 |
| `/api/favorites` | 用户收藏 |
| `/api/admin` | 统计数据/营收趋势/今日数据 |
| `/api/posters` | 海报管理 |
| `/api/payment` | 支付处理 |
| `/api/genres` | 电影类型查询 |
| `/api/hall-layouts` | 影厅座位布局 |
| `/api/operation-logs` | 操作日志查询 |
| `/api/files` | 文件下载/视频流播放（Range 支持） |

## 功能亮点

### 用户端
- 电影浏览与搜索（300ms 防抖 + 类型筛选 + 分页）
- 电影详情（预告片流播放 + 相似推荐 + 评分评论）
- 在线选座（动态影厅布局渲染 + 乐观锁防并发）
- 订单管理（待支付→已支付→已出票→已完成 全流程）
- 收藏系统 / 个人资料编辑 / 忘记密码

### 管理端
- 仪表盘（实时统计 + 订单趋势图 + 票房排行）
- 电影/场次/订单/用户 CRUD + 批量操作
- 海报上传管理 / 操作日志查询
- 表格排序 / 响应式侧边栏 + 移动端汉堡菜单

### 安全
- BCrypt 密码加密 + 环境变量管理敏感信息
- 7 层过滤器链 + 2 层拦截器 + AOP 操作审计
- XSS 防护 / 敏感词过滤 / 速率限制 (10000 req/min)
- 乐观锁防超卖 / 场次时间冲突检测

### 前端设计
- DM Sans + Noto Sans SC 中英文混排体系
- 完整暗色主题 + CSS 设计 token 体系
- 缓动动画系统 + prefers-reduced-motion 支持
- 低端设备自动禁用粒子特效
- ErrorBoundary 全局错误边界 + ARIA 无障碍

## Claude Code 工具链

本项目深度集成 Claude Code 工具生态：

| 工具 | 用途 |
|------|------|
| **5 个 Agent** | Backend / Frontend / QA / E2E / Data 独立职责 |
| **Impeccable** | 23 个前端设计命令（audit/critique/typeset/animate/polish...） |
| **git-ops** | 自动 stage→commit→push + 网络诊断 |
| **code-review** | PR 代码审查 |
| **code-simplifier** | 代码重构与简化 |
| **superpowers** | TDD / 调试 / 并行 Agent / 验证工作流 |
