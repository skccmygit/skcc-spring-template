## 필요 기능 리스트
- [X] API Response Spec 정의
- [X] 예외처리
  - [X] 예외 DTO 정의
  - [X] ControlAdvice 정의
- [X] 다국어기능 (message.Properties)
- [ ] 데이터 접근
  - [X] JPA
    - [X] Audit Entity (Create,Update 시)
    - [X] QueryDSL 의존성 추가
    - [X] JPA 쿼리 로그 (p6spy 적용)    
    - [X] QueryDSL 동적 쿼리 예시 추가
  - [ ] MyBatis
    - [X] Audit 인터셉터 (Create, Update 시)
    - [ ] Count 쿼리 인터셉터 ?
- [ ] Spring Security (인증,인가)
  - [ ] JWT Token
    - [X] JWT 요청 Filter (Stateless 정책) 
    - [X] AccessToken
    - [ ] RefreshToken 
      - [ ] Redis?
  - [X] UserDetailService
    - [X] MyCustomUserDetailService
  - [X] 핸들러
    - [X] 인증 실패 
    - [X] 인가 실패
- [ ] 로깅
  - [X] 콘솔 로그
    - [X] LogTraceId 필터 추가
    - [X] AOP 적용 및 CustomFormating (레벨 및 시그니처)
  - [ ] 거래 로그
    - [ ] 선/후 처리 인터셉터 구현?
- [X] 캐싱 
  - [X] 캐싱 로직 설계 (인터페이스)
  - [X] 기능
    - [X] put
    - [X] get
    - [X] evict
    - [X] clearAll
    - [X] clearCacheName
  - [X] 종류
    - [X] 카페인 캐시(로컬)
    - [X] Redis 캐시(서버)
  - [X] 샘플 작성
- [ ] 파일
  - [X] 업로드(단일,다중)
  - [ ] 다운로드
  - [X] 정책 (파일유형, 저장위치, 암호화여부)
  - [ ] 보안 (경로 및 파일명 암호화)
- [ ] 모니터링
- [ ] DevOps
  - [ ] GIT
  - [ ] Docker
  - [ ] k8s
  - [ ] CI/CD
- [ ] 가이드
    - [ ] 개발 환경 가이드
    - [ ] 개발 표준 가이드

---
## Minor 작업 리스트
- [ ] 테스트코드
- [ ] 테스트용 SQL 수정

---
## 시큐리티
1. 로그인  
1-1. JWT 토큰 생성
2. API 요청 (JWT Token을 포함)
3. JWT 요청 Filter   
3-1. 토큰 검증  
3-2. 토큰에서 uid 추출  
3-3. 추출된 uid로 사용자 조회  
3-4. 시큐리티 컨텍스트에 저장  
