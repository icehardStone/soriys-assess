FROM node:24-alpine AS build
WORKDIR /app

# 配置腾讯云 npm 镜像
RUN npm config set registry https://mirrors.cloud.tencent.com/npm/

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80