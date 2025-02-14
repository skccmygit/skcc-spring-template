## 사내망 SSL 설치 필요
## CMD %JAVA_HOME%
## PowerShell: $env:JAVA_HOME 

keytool -import -trustcacerts -file GlobalSign.crt -keystore $env:JAVA_HOME/lib/security/cacerts -storepass changeit -alias custom-cert

- SK 인증서 추가
  keytool -import -trustcacerts -file sk.crt -keystore $env:JAVA_HOME/lib/security/cacerts -storepass changeit -alias sk-cert

- 사내망의 경우 최종 ROOT 인증서를 keystore에 추가해줘야 함
- java가 실행하는 keystore에 추가해줘야함 
- java_home 확인 필수

---

- TEST_SK 인증서추가
  keytool -import -trustcacerts -file test_by_sk.crt -keystore $env:JAVA_HOME/lib/security/cacerts -storepass changeit -alias test-sk-cert

- TEST 인증서
  keytool -import -trustcacerts -file test.crt -keystore $env:JAVA_HOME/lib/security/cacerts -storepass changeit -alias test-cert

- 인증서확인
keytool -list -keystore $env:JAVA_HOME/lib/security/cacerts -storepass changeit

- 인증서삭제
keytool -delete -alias test-sk-cert -keystore $env:JAVA_HOME/lib/security/cacerts -storepass changeit