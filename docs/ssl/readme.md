# SK 사내망일 경우 SK ROOT 인증서 설치 필요

## 실행경로별 `JAVA_HOME` 세팅 명령어
- CMD: %JAVA_HOME%
- PowerShell: $env:JAVA_HOME

## 사전 설정 요소
- 환경설정에서 `JAVA_HOME` Path 설정
- SK Root 인증서 파일 (sk.crt)

## 인증서 명령어

1. 인증서 추가
   - powerShell
     ```bash
     keytool -import -trustcacerts -file '인증서 파일명' -keystore $env:JAVA_HOME/lib/security/cacerts -storepass changeit -alias '인증서 별칭'
     ```
   - cmd
     ```bash
     keytool -import -trustcacerts -file '인증서 파일명' -keystore %JAVA_HOME%/lib/security/cacerts -storepass changeit -alias '인증서 별칭'
     ```
2. 인증서 조회
   - powerShell
     ```bash
     keytool -list -keystore $env:JAVA_HOME/lib/security/cacerts -storepass changeit
     ```
   - cmd
     ```bash
     keytool -list -keystore %JAVA_HOME%/lib/security/cacerts -storepass changeit
     ```
3. 인증서 삭제
- powerShell
```bash
 keytool -alias 인증서별칭 -delete -keystore $env:JAVA_HOME/lib/security/cacerts -storepass changeit 
``` 




