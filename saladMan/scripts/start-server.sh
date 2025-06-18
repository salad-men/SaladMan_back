#!/bin/bash

echo "--------------- 서버 배포 시작 -----------------"
docker stop saladman-server || true
docker rm saladman-server || true
docker pull 362717844282.dkr.ecr.ap-northeast-2.amazonaws.com/saladman-server:latest
docker run -d --name saladman-server -p 8080:8080 362717844282.dkr.ecr.ap-northeast-2.amazonaws.com/saladman-server:latest
echo "--------------- 서버 배포 끝 -----------------"