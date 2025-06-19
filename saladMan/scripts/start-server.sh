#!/bin/bash
set -e

echo "======================================"
echo "      Starting full deploy process    "
echo "======================================"

# 1) 기존 컨테이너 중지 & 제거
echo ">> Stopping old container..."
docker stop saladman-server  || true
docker rm   saladman-server  || true

# 2) 최신 이미지 pull
echo ">> Pulling latest image..."
aws ecr get-login-password --region ap-northeast-2 \
  | docker login --username AWS --password-stdin 362717844282.dkr.ecr.ap-northeast-2.amazonaws.com
docker pull 362717844282.dkr.ecr.ap-northeast-2.amazonaws.com/saladman-server:latest

# 3) 새 컨테이너 실행
echo ">> Running new container..."
docker run -d \
  --name saladman-server \
  --restart always \
  -p 80:8090 \
  362717844282.dkr.ecr.ap-northeast-2.amazonaws.com/saladman-server:latest

# 4) Nginx 설정 문법 확인 & 재시작
echo ">> Testing Nginx configuration..."
nginx -t

echo ">> Reloading Nginx..."
systemctl reload nginx

echo "======================================"
echo "      Deploy process completed        "
echo "======================================"
