# soriys-assess

## Docker Compose 部署

1. 复制环境变量示例并设置一个安全的数据库密码：`cp .env.example .env`。
2. 构建并在后台启动服务：`docker compose up -d --build`。
3. 打开 `http://localhost`（或 `.env` 中的 `WEB_PORT`）。前端会将 `/api` 请求转发到后端服务。

首次启动时 PostgreSQL 会自动执行 `schema.sql` 创建表。数据库数据存储在具名卷 `postgres_data` 中；如需完全重置开发数据，执行 `docker compose down -v` 后重新启动。
