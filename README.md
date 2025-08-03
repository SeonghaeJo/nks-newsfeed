# 뉴스피드 시스템 구축

### Infra
- Ncloud Kubernetes Service 기반
  - 클러스터 전체 자원: vCPU 16EA, Memory 32GB
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

### Data Consistency
#### Saga Pattern for MySQL-Neo4j Synchronization
- **Problem**: MySQL(사용자 정보)과 Neo4j(친구 관계) 간 데이터 일관성 보장 필요
- **Solution**: Event-driven Saga Pattern with compensation logic
- **Flow**:
  1. MySQL에 User 저장 후 UserCreatedEvent 발행
  2. @TransactionalEventListener로 Neo4j 동기화 시도
  3. @Retryable(3회, backoff 1s→2s→4s)로 재시도
  4. 실패 시 @Recover에서 MySQL 데이터 삭제 (보상 트랜잭션)
- **Result**: Eventually Consistent 데이터 상태 보장

### Naver Cloud Platform Configuration
- Public NAT Gateway 생성 후 Private Subnet 을 위한 Route Table 에 모든 목적지 IP에 대한 NATGW Target Route를 추가해야 한다.
  - NKS Pod 들은 Private Subnet 안에 생성되기 때문에, 인터넷 아웃바운드 통신 (외부 레지스트리 접근 등)을 위해 필요하다.
- TCP 인바운드/아웃바운드 차단 규칙이 없어야 quay.io (container image registry of RedHat) 접속이 된다.

### ArgoCD 설치 및 연동

#### 1. ArgoCD 설치
```bash
# ArgoCD namespace 생성
kubectl create namespace argocd

# ArgoCD 설치 (공식 매니페스트)
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

# ArgoCD를 위한 NKS LoadBalancer (L4 수준) 서비스 생성
kubectl apply -f k8s/argocd/install.yaml
```

#### 2. ArgoCD 접속 설정
```bash
# ArgoCD admin 비밀번호 확인
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d

# LoadBalancer 외부 IP 확인
kubectl get svc -n argocd argocd-server-lb
```

#### 3. ArgoCD 웹 UI 접속
- URL: `http://<LoadBalancer-External-IP>`
- Username: `admin`
- Password: 위에서 확인한 비밀번호

