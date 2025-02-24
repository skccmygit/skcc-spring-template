# Logging Guide
> 이 문서는 서비스에서 로그를 구성하고 사용하는 방법에 대해 설명합니다.
> 로그를 통해 애플리케이션의 상태를 파악하고 디버깅할 수 있으며, 이 가이드는 `Logback` 기반으로 Spring Boot 프로젝트에서 로그를 효과적으로 사용하는 방법과 설정에 대해 다룹니다.
> 프로젝트 성격에 맞게 커스터마이징 하여 사용하시면 됩니다.

---

## 목차
1. [로그 구성 요소](#1-로그-구성-요소)
   - [1.1. LogFormatAop](#11-logformataopjava)  
   - [1.2. LogFormatUtil](#12-logformatutiljava)  
   - [1.3. LogTraceIdFilter](#13-logtraceidfilterjava)  
2. [로그 실행 흐름](#2-로그-사용-방법)
3. [로그 환경 구성](#3-로그-주요-구성-applicationyml)  
   - [3.1. 로그 레벨](#31-로깅-레벨)  
   - [3.2. 로그 포맷](#32-로그-포맷)
4. [로그 사용 예제](#4-로그-사용-예제)

---

## 1. **로그 구성 요소**

### 1.1. **LogFormatAop.java**
- 서비스 계층 또는 특정 메서드 호출 시점의 **Depth**를 관리하고, 메서드 호출의 lifecycle를 추적합니다.
- AOP(Aspect-Oriented Programming)를 활용하여 클래스와 메서드 호출을 가로채 로그 메시지를 자동으로 포맷 설정합니다.
- 주요 기능:
    - 메서드 호출 Depth 관리 (`Depth` 추가/해제)
    - @Around를 활용한 메서드 호출 전후 처리를 기반으로한 Log 트레이싱.

---

### 1.2. **LogFormatUtil.java**
- 로그 포맷 및 Depth, MDC 값을 관리하는 유틸리티 클래스입니다.
- 주요 기능:
    - MDC (Mapped Diagnostic Context)를 활용하여 **TraceId** 및 **Depth** 값을 중앙에서 관리.
    - `initializeDepth`, `incrementDepth`, `decrementDepth`, `clearDepth` 등 Depth 관리 기능 제공.
    - 로그 수준에 따른 로그를 포맷할 수 있도록 설정.

---

### 1.3. **LogTraceIdFilter.java**
- 각 요청에 대해 고유한 **TraceId**를 생성해 MDC에 저장합니다.
- HTTP 요청마다 **서로 다른 고유값**을 생성하여, 요청과 로그를 분리하여 추적할 수 있도록 합니다.
- 주요 역할:
    - 고유 시간이 담긴 `TraceId` 값 관리
    - MDC에 요청별 TraceId를 저장 및 요청 완료 후 초기화.

---

## 2. **로그 실행 흐름**
1. **HTTP 요청**이 들어오면 `LogTraceIdFilter`가 실행되어 고유 `TraceId`가 생성됩니다.
2. `LogFormatAop`를 통해 메서드 호출 시점마다 Depth 및 시그니처를 추적하고 로그 패턴을 구성합니다.
3. `LogFormatUtil`이 MDC와 Depth 관련 관리를 수행하며, 이를 바탕으로 정해진 로그 포맷에 따라 메시지를 출력합니다.
4. 최종 결과는 **콘솔 로그**나 **파일 로그**로 출력됩니다.
---

## 3. **로그 주요 구성 (application.yml)**
- YAML 파일을 통해 로그 패턴, 로그 레벨, 출력 방식 등을 설정합니다.
- 설정 요소:
  - **로그 레벨**: `INFO`, `DEBUG`, `ERROR`, `WARN` 등을 설정할 수 있으며, 특정 패키지에 대한 로그 레벨 제어 가능.
  - **로그 패턴**: 콘솔 및 파일에 출력되는 로그 메시지의 형식을 지정.
  - **파일 로깅**: 로그를 파일에 저장하도록 추가 설정 가능.

### 3.1. **로깅 레벨**

- 로그 레벨을 사용하여 애플리케이션 실행 중 출력할 로그의 범위를 제어합니다.
- `application.yml`에서 전역 또는 특정 패키지의 로그 레벨을 설정할 수 있습니다.

```yaml
logging:
  level:
    root: info          # 기본 로그 레벨: INFO
    skcc.arch: debug    # 특정 패키지 로그 레벨은 DEBUG
```

#### 기본 로그 레벨:
- `trace`: 가장 상세한 로그, 개발 중 디버깅용.
- `debug`: 디버깅을 위한 정보.
- `info`: 일반적인 실행 정보.
- `warn`: 경고 메시지.
- `error`: 실패 또는 치명적인 문제.

---

### 3.2. **로그 포맷**

#### 콘솔 로그 패턴
`console` 영역을 통해 콘솔 로그의 형식을 정의합니다.

```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread][%highlight(%-5level)]%X{traceId}%X{depth} %msg%n"
```

#### 포맷 구성 요소
- `%d{yyyy-MM-dd HH:mm:ss}`: 로그의 출력 시간 (예: `2025-02-13 15:52:22`)
- `%thread`: 로그를 발생시킨 스레드 이름.
- `%highlight(%-5level)`: 로그 수준 표시 (`INFO`, `DEBUG`, `ERROR` 등).
- `%X{traceId}`: 요청별 고유 TraceId (MDC에서 관리).
- `%X{depth}`: 서비스 계층 및 메서드의 Depth Decorate 및 Signature
- `%msg`: 실제 출력 메시지.
- `%n`: 줄바꿈.

#### 파일 로그 설정 (옵션)
아래 설정을 추가하면 로그를 파일로 저장할 수 있습니다.  
더 자세한 내용은 SpringBoot Docs를 참고 하시면 됩니다.   
[SpringBoot Loging Docs 바로가기](https://docs.spring.io/spring-boot/reference/features/logging.html#features.logging.logback-extensions.profile-specific)
```yaml
logging:
  pattern:
# 로그 파일 관리
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread][%highlight(%-5level)]%X{traceId}%X{depth} %msg%n" # 콘솔 로그 패턴
  file:
    name: logs/app.log # 파일 로그 경로 지정
  logback:
    rollingpolicy:
      file-name-pattern: ${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz
      max-file-size: 10MB
```

---

## 4. **로그 사용 예제**
- lombok 어노테이션인 @Slf4j 을 이용하여 로그를 출력합니다.

#### Controller
```java
@RestController
@RequestMapping("/api/log")
@Slf4j
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @GetMapping("/1")
    public void getLog1() {
        String msg = "1번 로그입니당";
        log.debug("컨트롤러 호출 시작 : {}", msg);
        logService.logTest();
        log.debug("컨트롤러 호출 종료");
    }

    @GetMapping("/2")
    public void getLog2() {
        String msg = "2번 로그입니당";
        log.debug("컨트롤러 호출 시작 : {}", msg);
        logService.logTest2();
        log.debug("컨트롤러 호출 종료");
    }
}
```

#### Service
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {

    private final LogRepository logRepository;

    public void logTest() {
        log.debug("Service 로그 호출 시작 ");
        logRepository.logTest(100);
        log.debug("Repository 재호출 ");
        logRepository.logTest(100);
        log.debug("Service 로그 호출 종료 ");
    }

    public void logTest2() {
        log.debug("Service 로그 호출 시작 ");
        logRepository.logTest(200);
        log.debug("Service 로그 호출 종료 ");
    }
}

```

#### Repository
```java
@Repository
@Slf4j
public class LogRepository {

    public void logTest(int number) {
        log.debug("Repository 로그 시작");
        log.debug("Repository 로그 입니다 : {}", number);
        log.debug("Repository 로그 종료");

    }
}
```

---

### 예제 출력 (콘솔 로그)
```
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ LogController.getLog1] 컨트롤러 호출 시작 : 1번 로그입니당
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ |-->LogService.logTest] Service 로그 호출 시작 
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ | |-->LogRepository.logTest] Repository 로그 시작
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ | |-->LogRepository.logTest] Repository 로그 입니다 : 100
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ | |-->LogRepository.logTest] Repository 로그 종료
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ |<--LogService.logTest] Repository 재호출 
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ | |-->LogRepository.logTest] Repository 로그 시작
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ | |-->LogRepository.logTest] Repository 로그 입니다 : 100
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ | |-->LogRepository.logTest] Repository 로그 종료
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ |<--LogService.logTest] Service 로그 호출 종료 
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ LogController.getLog1] 컨트롤러 호출 종료
```

---