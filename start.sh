#!/bin/bash

# 切换到工作目录
cd /app

# 构建 Docker 镜像
docker build -t hxbi:v1 .

# 运行容器
docker run -d -p 6848:6848 --name hxbi hxbi:v1