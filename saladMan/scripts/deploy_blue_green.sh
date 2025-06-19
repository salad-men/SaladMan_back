#!/bin/bash
set -e

#----------------------------------------
# Blue/Green 무중단 배포 스크립트
#----------------------------------------

# 0) 변수 설정
BLUE_PORT=8091
GREEN_PORT=8092
ACTIVE_FILE=/home/ubuntu/saladman-server/active_port
ECR_REGISTRY=362717844282.dkr.ecr.ap-northeast-2.amazonaws.com
IMAGE_NAME=saladman-server:latest

# 1) 현재 활성 포트 확인 (없으면 BLUE 기본1)
if [ -f "$ACTIVE_FILE" ]; then
  CURRENT_PORT=$(cat "$ACTIVE_FILE")
else
  CURRENT_PORT=$BLUE_PORT
fi

# 2) 새로 기동할 포트 결정
if [ "$CURRENT_PORT" -eq "$BLUE_PORT" ]; then
  NEXT_PORT=$GREEN_PORT
else
  NEXT_PORT=$BLUE_PORT
fi

echo ""
echo "🔵 CURRENT_PORT = $CURRENT_PORT"
echo "🟢 NEXT_PORT    = $NEXT_PORT"
echo "🚀 Starting Blue/Green deployment..."

# 3) 기존 동일 이름 컨테이너가 남아 있다면 제거
echo "🧹 Removing existing container with name saladman-$NEXT_PORT (if any)"
docker rm -f saladman-$NEXT_PORT || true

# 4) 새 컨테이너 실행
echo "🐳 Pulling & Running container on port $NEXT_PORT"
docker pull $ECR_REGISTRY/$IMAGE_NAME
docker run -d \
  --name saladman-$NEXT_PORT \
  --restart always \
  -p $NEXT_PORT:8090 \
  $ECR_REGISTRY/$IMAGE_NAME

# 5) Health 체크
echo "💓 Running health check on http://localhost:$NEXT_PORT/actuator/health"
for i in {1..12}; do
  if curl -sSf http://localhost:$NEXT_PORT/actuator/health >/dev/null; then
    echo "✅ Health check succeeded!"
    break
  fi
  echo "   ⏳ retry $i..."
  sleep 5
  if [ $i -eq 12 ]; then
    echo "❌ Health check failed. Aborting deployment."
    exit 1
  fi
done

# 6) Nginx proxy 전환
echo "🔁 Switching Nginx upstream to port $NEXT_PORT"
cat <<EOF > /etc/nginx/conf.d/upstream-saladman.conf
upstream saladman_backend {
    server 127.0.0.1:$NEXT_PORT;
}
EOF

echo "📎 Nginx config:"
cat /etc/nginx/conf.d/upstream-saladman.conf

echo "📦 Reloading nginx..."
nginx -t
systemctl reload nginx

# 7) 이전 컨테이너 정리
echo "🗑 Stopping & Removing old container: saladman-$CURRENT_PORT"
docker stop saladman-$CURRENT_PORT || true
docker rm   saladman-$CURRENT_PORT || true

# 8) 포트 기록 갱신
echo "$NEXT_PORT" > "$ACTIVE_FILE"

echo "✅ Blue/Green Deployment Completed: Now serving on port $NEXT_PORT"
