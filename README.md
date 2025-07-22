# 🥗 샐러드맨(SaladMan)
**샐러드 매장 통합 관리 플랫폼**

> **본사-매장-고객**을 하나로 연결하는 재고/발주/매출/인사/소통/챗봇/키오스크 통합 서비스

---
![매장- 직원 일정관리](https://github.com/user-attachments/assets/30a7cb61-efcd-479f-958b-2890655e8717)

## 목차
1. [프로젝트 개요](#프로젝트-개요)
2. [팀원 및 담당 업무](#팀원-및-담당-업무)
3. [기술스택 & 아키텍처](#기술스택--아키텍처)
4. [사용 기술](#사용-기술)
5. [서비스 주요 기능](#서비스-주요-기능)
6. [주요 프로세스](#주요-프로세스)
7. [기술적 요소](#기술적-요소)
8. [트러블슈팅](#트러블슈팅)
9. [데이터베이스 설계](#데이터베이스-설계)
10. [개발화면 및 시연](#개발화면-및-시연)
11. [Q&A](#qa)

---

## 프로젝트 개요

- **샐러드 시장 연 8.4% 성장**  
- **재고/발주/매출/인사/소통** 등 매장 운영 전반 통합관리
- **본사-직영점 단일 시스템**으로 업무 효율화
- **입고-소진-폐기-발주 자동화 및 실시간 모니터링**

---

## 아키텍처
<img width="1631" height="918" alt="image" src="https://github.com/user-attachments/assets/ed7789b5-16d3-42a8-95c9-360530795bfe" />

---

## 🛠️ 기술스택

### FrontEnd
<div>
  <img src="https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB"/>
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black"/>
</div>

### BackEnd
<div>
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring_Data_JPA-007396?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/QueryDSL-003B57?style=for-the-badge"/>
</div>

### 설계/협업 Tool
<div>
  <img src="https://img.shields.io/badge/ERD_Cloud-5A29E4?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Google_Sheets-34A853?style=for-the-badge&logo=google-sheets&logoColor=white"/>
  <img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white"/>
  <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white"/>
</div>

### Infra / Cloud
<div>
  <img src="https://img.shields.io/badge/Amazon_EC2-FF9900?style=for-the-badge&logo=amazon-ec2&logoColor=white"/>
  <img src="https://img.shields.io/badge/AWS_CloudFront-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white"/>
  <img src="https://img.shields.io/badge/AWS_S3-569A31?style=for-the-badge&logo=amazon-s3&logoColor=white"/>
</div>

### 외부 API & 기타
<div>
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black"/>
  <img src="https://img.shields.io/badge/toss%20payments-0064FF?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/KakaoMap-FFCD00?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/ZXING%20QR코드-4285F4?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white"/>
</div>

### 개발환경/IDE
<div>
  <img src="https://img.shields.io/badge/VS_Code-007ACC?style=for-the-badge&logo=visual-studio-code&logoColor=white"/>
  <img src="https://img.shields.io/badge/STS-6DB33F?style=for-the-badge"/>
</div>

### CI/CD & 형상관리
<div>
  <img src="https://img.shields.io/badge/Github-181717?style=for-the-badge&logo=github&logoColor=white"/>
  <img src="https://img.shields.io/badge/Github_Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"/>
</div>

### Database
<div>
  <img src="https://img.shields.io/badge/HeidiSQL-0055A4?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>
  <img src="https://img.shields.io/badge/Amazon_RDS-527FFF?style=for-the-badge&logo=amazon-rds&logoColor=white"/>
</div>



---
## 📦 서비스 주요 기능 (액터별)

---

### 1. 본사(관리자) 기능

<details>
<summary> <img src="./images/hq-inventory.png" width="22"/> <strong>본사 - 재고</strong> </summary>

| ![](./images/hq-inventory1.png) | ![](./images/hq-inventory2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

- 재고관리
- 유통기한 관리
- 폐기재료/폐기요청재료
- 재료설정
- 입/출고내역
- 카테고리/재료관리

</details>

<details>
<summary> <img src="./images/hq-order.png" width="22"/> <strong>본사 - 수주</strong> </summary>

| ![](./images/hq-order1.png) | ![](./images/hq-order2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |
![매장-유통기한 관리](https://github.com/user-attachments/assets/1a54ccd2-7855-4288-8368-4a66af80cba3)

- 수주목록
- 수주상세
- 발주서 출력
- 재료별 수주 가능여부 설정

</details>

<details>
<summary> <img src="./images/hq-menu.png" width="22"/> <strong>본사 - 메뉴</strong> </summary>

| ![](./images/hq-menu1.png) | ![](./images/hq-menu2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

- 전체메뉴
- 메뉴등록
- 레시피조회

</details>

<details>
<summary> <img src="./images/hq-sales.png" width="22"/> <strong>본사 - 매출</strong> </summary>

| ![](./images/hq-sales1.png) | ![](./images/hq-sales2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

- 매출조회(전사)
- 매출조회(지점)

</details>

<details>
<summary> <img src="./images/hq-store.png" width="22"/> <strong>본사 - 매장관리</strong> </summary>

| ![](./images/hq-store1.png) | ![](./images/hq-store2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

- 매장목록/매장등록/매장상세
- 직원목록/직원상세/직원등록

</details>

<details>
<summary> <img src="./images/hq-notice.png" width="22"/> <strong>본사 - 공지사항</strong> </summary>

| ![](./images/hq-notice1.png) | ![](./images/hq-notice2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

- 공지사항
- 불편사항
- 알림 목록

</details>

<details>
<summary> <img src="./images/hq-dashboard.png" width="22"/> <strong>본사 - 대시보드</strong> </summary>

| ![](./images/hq-dashboard1.png) | ![](./images/hq-dashboard2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

</details>

---

### 2. 매장(직영점) 기능

<details>
<summary> <img src="./images/store-inventory.png" width="22"/> <strong>매장 - 재고</strong> </summary>

| ![](./images/store-inventory1.png) | ![](./images/store-inventory2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

- 재고관리
- 유통기한 관리
- 폐기신청 목록
- 재료 입고내역
- 재료사용내역
- 재료설정

</details>

<details>
<summary> <img src="./images/store-order.png" width="22"/> <strong>매장 - 발주</strong> </summary>

| ![](./images/store-order1.png) | ![](./images/store-order2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

- 발주목록
- 발주신청
- 자동발주설정

</details>

<details>
<summary> <img src="./images/store-menu.png" width="22"/> <strong>매장 - 메뉴</strong> </summary>

| ![](./images/store-menu1.png) | ![](./images/store-menu2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

- 전체메뉴조회
- 점포메뉴관리
- 레시피조회

</details>

<details>
<summary> <img src="./images/store-manage.png" width="22"/> <strong>매장 - 매장관리</strong> </summary>

| ![](./images/store-manage1.png) | ![](./images/store-manage2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

- 매출조회
- 주문 및 환불 내역 조회
- 직원목록/직원상세/직원 일정관리(테이블, 캘린더)

</details>

<details>
<summary> <img src="./images/store-location.png" width="22"/> <strong>매장 - 점포조회</strong> </summary>

| ![](./images/store-location1.png) | ![](./images/store-location2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

- 타 매장 재고 조회

</details>

<details>
<summary> <img src="./images/store-notice.png" width="22"/> <strong>매장 - 공지사항</strong> </summary>

| ![](./images/store-notice1.png) | ![](./images/store-notice2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

- 공지사항
- 불편사항
- 알림목록

</details>

<details>
<summary> <img src="./images/store-dashboard.png" width="22"/> <strong>매장 - 대시보드</strong> </summary>

| ![](./images/store-dashboard1.png) | ![](./images/store-dashboard2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

</details>

---

### 3. 본사 & 매장 공통

<details>
<summary> <img src="./images/login.png" width="22"/> <strong>로그인</strong> </summary>

| ![](./images/login1.png) | ![](./images/login2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

</details>

<details>
<summary> <img src="./images/chat.png" width="22"/> <strong>채팅</strong> </summary>

| ![](./images/chat1.png) | ![](./images/chat2.png) |
|:---:|:---:|
| 내 채팅목록/그룹채팅목록 | 매장목록/채팅화면 |

</details>

<details>
<summary> <img src="./images/alarm.png" width="22"/> <strong>알림</strong> </summary>

| ![](./images/alarm1.png) | ![](./images/alarm2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

</details>

---

### 4. 사용자

<details>
<summary> <img src="./images/user-main.png" width="22"/> <strong>메인페이지</strong> </summary>

| ![](./images/user-main1.png) | ![](./images/user-main2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

</details>

<details>
<summary> <img src="./images/user-brand.png" width="22"/> <strong>브랜드 소개/메뉴</strong> </summary>

| ![](./images/user-brand1.png) | ![](./images/user-brand2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

</details>

<details>
<summary> <img src="./images/user-nutrition.png" width="22"/> <strong>영양표/매장찾기</strong> </summary>

| ![](./images/user-nutrition1.png) | ![](./images/user-nutrition2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

</details>

<details>
<summary> <img src="./images/user-news.png" width="22"/> <strong>새소식</strong> </summary>

| ![](./images/user-news1.png) | ![](./images/user-news2.png) |
|:---:|:---:|
| 공지사항/이벤트 | 칭찬매장 |

</details>

<details>
<summary> <img src="./images/user-chatbot.png" width="22"/> <strong>챗봇</strong> </summary>

| ![](./images/user-chatbot1.png) | ![](./images/user-chatbot2.png) |
|:---:|:---:|
| 메뉴/매장찾기/불편사항 | 자주묻는질문 |

</details>

<details>
<summary> <img src="./images/user-banner.png" width="22"/> <strong>배너/이미지 수정(본사 전용)</strong> </summary>

| ![](./images/user-banner1.png) | ![](./images/user-banner2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

</details>

---

### 5. 키오스크

<details>
<summary> <img src="./images/kiosk-type.png" width="22"/> <strong>이용유형 선택</strong> </summary>

| ![](./images/kiosk-type1.png) | ![](./images/kiosk-type2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

</details>

<details>
<summary> <img src="./images/kiosk-menu.png" width="22"/> <strong>메뉴 선택/카트담기</strong> </summary>

| ![](./images/kiosk-menu1.png) | ![](./images/kiosk-menu2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

</details>

<details>
<summary> <img src="./images/kiosk-order.png" width="22"/> <strong>주문서/결제</strong> </summary>

| ![](./images/kiosk-order1.png) | ![](./images/kiosk-order2.png) |
|:---:|:---:|
| 예시이미지1 | 예시이미지2 |

</details>




---

## 주요 프로세스

<details>
<summary><strong>🍱 매장 발주 프로세스</strong></summary>

<details>
<summary>1️⃣ 재고 확인</summary>
매장 내 현재 재고량 실시간 확인
</details>
<details>
<summary>2️⃣ 발주 신청</summary>
부족한 재고의 발주 신청
</details>
<details>
<summary>3️⃣ 본사 수주 승인</summary>
본사에서 각 매장 발주 신청 확인 및 승인
</details>
<details>
<summary>4️⃣ 입고 검수</summary>
발주 입고 도착 시 검수 및 완료 처리
</details>
<details>
<summary>5️⃣ 발주 내역/자동발주/이력 관리</summary>
발주 이력, 자동발주 설정, 히스토리 관리
</details>
</details>

---

<details>
<summary><strong>♻️ 폐기 프로세스</strong></summary>

<details>
<summary>1️⃣ 유통기한 확인</summary>
보유 재고의 유통기한 실시간 확인
</details>
<details>
<summary>2️⃣ 폐기 신청 및 사유작성</summary>
폐기할 품목, 사유 작성 및 신청
</details>
<details>
<summary>3️⃣ 본사 폐기 승인/관리</summary>
본사에서 폐기 요청 승인 및 관리
</details>
<details>
<summary>4️⃣ 폐기 상태 추적(대기/완료)</summary>
폐기 상태(대기/완료) 실시간 추적 및 이력 관리
</details>
</details>

---

<details>
<summary><strong>🥗 메뉴 등록/설정 프로세스</strong></summary>

<details>
<summary>1️⃣ 신규 메뉴/레시피 등록</summary>
새로운 메뉴 및 상세 레시피 등록
</details>
<details>
<summary>2️⃣ 매장별 메뉴 판매 ON/OFF 설정</summary>
매장별 메뉴 판매 설정 관리
</details>
</details>

---

<details>
<summary><strong>🏬 매장 및 직원 등록 프로세스</strong></summary>

<details>
<summary>1️⃣ 매장 등록</summary>
신규 매장 등록 및 정보 입력
</details>
<details>
<summary>2️⃣ 직원 등록 및 배정</summary>
직원 등록 및 매장 배정, 권한 할당
</details>
<details>
<summary>3️⃣ 직무/권한 관리</summary>
직원별 직무, 권한 부여 및 관리
</details>
</details>

---

## 기술적 요소

<details>
<summary><strong>💬 채팅 서비스</strong></summary>

</details>

<details>
<summary><strong>📷 QR코드</strong></summary>
<img width="1696" height="765" alt="image" src="https://github.com/user-attachments/assets/9da72f00-043c-4dcc-9ec5-cab76597dbfd" />

</details>

<details>
<summary><strong>🔔 알림 (FCM)</strong></summary>
FCM 기반 주요 이벤트 푸시알림, 템플릿 자동 발송, 토큰 발급·관리
</details>

<details>
<summary><strong>🔐 인증/보안 (Spring Security)</strong></summary>
JWT 기반 인증, 세션리스 구조, 토큰 만료/재발급/로컬스토리지 일원화, SPA 환경
</details>

<details>
<summary><strong>📷 QR코드</strong></summary>
<img width="1696" height="765" alt="image" src="https://github.com/user-attachments/assets/9da72f00-043c-4dcc-9ec5-cab76597dbfd" />

</details>

---

## 트러블슈팅

| 🏷️ 이슈 |   문제 & 원인   |   해결 방법 & 결과   |
|:--------|:---------------|:---------------------|
| **AWS 무중단 배포** | <br>배포 시 트래픽 단절·다운타임 발생<br> | <br>**Docker Blue/Green** 컨테이너 병렬 운영, <br>**AWS CodeDeploy + Nginx** 트래픽 스위칭 적용<br> |
| **실시간 채팅 성능** | <br>HTTP 폴링 기반 채팅 TPS 급증/부하<br> | <br>**WebSocket+STOMP** 전환, Persistent Connection, 메시지 브로커 구독/발행<br> |
| **배포 자동화/CI-CD** | <br>배포 자동화 중 환경변수/권한/빌드 오류<br> | <br>**Github Actions → S3/ECR → CodeDeploy EC2**<br> 환경 분리, 빌드상태 슬랙/이메일 실시간 알림<br> |
| **재고/주문 트랜잭션** | <br>결제 승인 중 재고 부족 예외 발생시 품절처리까지 롤백<br> | <br>품절처리 로직 `@Transactional(REQUIRES_NEW)`로 분리, <br>트랜잭션 독립적 처리, 데이터 정합성 보장<br> |
| **알림 발송 구조** | <br>발신자/수신자 수동 입력, 템플릿 미사용<br> | <br>**알림 템플릿/자동 발송 구조**로 리팩토링<br> |
| **시큐리티/인증 동기화** | <br>토큰 만료·탈취·불일치 시 자동 만료 처리 안됨<br> | <br>**Axios 인터셉터 401시 자동 로그아웃/재인증,**<br> Refresh 토큰 동기화, 변조 시 즉시 만료<br> |

---

> 💡 표+이모지+볼드 조합으로 “한눈에 한 줄”로 핵심만,  
> 필요시 각 이슈에 `<details>`로 상세설명(원인/코드/흐름도 등)도 넣을 수 있습니다.

---

### 참고

- 중첩 토글 시 **내부 토글은 summary 앞에 이모지/숫자/볼드 등으로 구분**하면 가독성 최고!
- 트러블슈팅은 표로 한눈에, 또는 “문제 → 해결” 인라인 토글/리스트도 활용 가능
- 이 포맷에서 원하면 더 예쁘게 커스텀(색/박스 등) 추가도 가능

---

원하는 형태/톤 더 커스터마이징 원하면 바로 말씀해 주세요!  
(더 미니멀/더 화려/컬러박스/예시 코드/탭 등 가능!)


## 데이터베이스 설계

- **테이블 수:** 31개
- **주요 테이블:** 사용자, 매장, 직원, 재고, 발주/수주, 메뉴, 레시피, 폐기, 공지, 알림, 채팅 등  
- (ERD/상세 테이블은 [ERD Cloud/HeidiSQL 설계문서](#) 참고)

---

## 개발화면 및 시연

> **아래에 시연 이미지, 캡처, 영상을 넣으세요!**

- **관리자/본사:**  
  ![관리자-시연-이미지](#)
- **매장(직영점):**  
  ![매장-시연-이미지](#)
- **고객(키오스크/챗봇):**  
  ![고객-시연-이미지](#)
- **채팅/알림/공지 등:**  
  ![채팅-시연-이미지](#)

---

## Q&A

문의: [ekzm849@naver.com](mailto:ekzm849@naver.com)  
BackEnd: [github.com/salad-men/SaladMan_back](https://github.com/salad-men/SaladMan_back)  
FrontEnd: [github.com/salad-men/SaladMan_front](https://github.com/salad-men/SaladMan_front)

---

> **노션, ERD, API 문서 등 링크/자료는 필요시 추가!**
