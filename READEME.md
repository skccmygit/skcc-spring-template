

# 쿠버네티스 

kubectl apply -f ./deploy_thymeleaf.yml
kubectl apply -f ./service_thymeleaf.yml


## 시큐리티

1. 로그인  
1-1. JWT 토큰 생성
2. API 요청 (JWT Token을 포함)
3. JWT 요청 Filter   
3-1. 토큰 검증  
3-2. 토큰에서 uid 추출  
3-3. 추출된 uid로 사용자 조회  
3-4. 시큐리티 컨텍스트에 저장  
