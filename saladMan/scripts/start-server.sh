#!/bin/bash

echo "--------------- 서버 배포 시작 -----------------"
sudo docker stop saladman-server || true
sudo docker rm saladman-server || true
sudo docker pull 362717844282.dkr.ecr.ap-northeast-2.amazonaws.com/saladman-server:latest
sudo docker run -d --name saladman-server -p 80:8090 362717844282.dkr.ecr.ap-northeast-2.amazonaws.com/saladman-server:latest
echo "--------------- 서버 배포 끝 -----------------"