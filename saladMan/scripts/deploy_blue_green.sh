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

# 1) 현재 활성 포트 확인 (없으면 BLUE 기본)
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

echo ">>> Deploying to port $NEXT_PORT (현재: $CURRENT_PORT)"

# 3) 컨테이너 실행
echo ">> Pull & run container on $NEXT_PORT"
aws ecr get-login-password --region ap-northeast-2 \
  | docker login --username AWS --password-stdin $ECR_REGISTRY

docker pull $ECR_REGISTRY/$IMAGE_NAME
docker run -d \
  --name saladman-$NEXT_PORT \
  --restart always \
  -p $NEXT_PORT:8090 \
  $ECR_REGISTRY/$IMAGE_NAME

# 4) Health 체크 (최대 12회, 5초 간격)
echo ">> Health check on port $NEXT_PORT"
for i in {1..12}; do
  if curl -sSf http://localhost:$NEXT_PORT/actuator/health >/dev/null; then
    echo "   → OK"
    break
  fi
  echo "   → retry $i"
  sleep 5
  if [ $i -eq 12 ]; then
    echo "!!! Health check failed. Aborting."
    exit 1
  fi
done

# 5) Nginx upstream 설정 교체 & reload
echo ">> Updating Nginx upstream to $NEXT_PORT"
cat <<EOF > /etc/nginx/conf.d/upstream-saladman.conf
upstream saladman_backend {
    server 127.0.0.1:$NEXT_PORT;
}
EOF

nginx -t
systemctl reload nginx

# 6) 이전 컨테이너 정리
echo ">> Stopping old container on port $CURRENT_PORT"
docker stop saladman-$CURRENT_PORT || true
docker rm   saladman-$CURRENT_PORT || true

# 7) 활성 포트 기록 갱신
echo $NEXT_PORT > "$ACTIVE_FILE"

echo ">>> Deployment to $NEXT_PORT completed."
