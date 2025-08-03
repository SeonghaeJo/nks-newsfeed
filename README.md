# 뉴스피드 시스템 구축

### Infra
- Ncloud Kubernetes Service 기반
  - 클러스터 전체 자원: vCPU 16EA, Memory 64GB
  - Naver Cloud Platform Application LoadBalancer (NCP ALB)를 Kubernetes Ingress 로 사용
  - Naver Cloud Platform BlockStorage 를 k8s Storage Class 로 사용
  - ArgoCD
- 메시지 큐 : Rabbit MQ Cluster / Spring AMQP
  - k8s Rabbit MQ Operator 사용
- 포스트, 사용자 정보, 뉴스 피드 캐시 : Redis Cluster
  - k8s Redis Operator 사용
- 분산 애플리케이션 Rate Limiting : Redis Bucket4j
- 그래프 데이터베이스 : Neo4j
- 포스팅 / 사용자 정보 데이터베이스 : MySQL

### Architecture
- 가상면접 사례로 배우는 대규모 시스템 설계 기초 1 / 11장 기반
![newfeed-arch.png](newfeed-arch.png)

### Backend Application
#### Web Application
- 포스팅 저장 / 전송 / 인증 / 친구 관리
  - PostService : 포스트 CRUD
  - PostTransmitService : 포스트를 메시지 큐로 전송
  - UserService : 사용자 CRUD
  - FriendService : 친구 CRUD
  - AuthService : JWT 인증
#### Post Distribution Worker
- 포스트 전송 작업 서버
