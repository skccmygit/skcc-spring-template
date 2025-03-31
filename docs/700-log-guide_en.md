# Logging Guide
> This document explains how to configure and use logging in the service.
> Through logs, you can understand the application's state and debug issues. This guide covers how to effectively use and configure logs in a Spring Boot project based on `Logback`.
> You can customize it according to your project's needs.

---

## Table of Contents
1. [Log Components](#1-log-components)
   - [1.1. LogFormatAop](#11-logformataopjava)  
   - [1.2. LogFormatUtil](#12-logformatutiljava)  
   - [1.3. LogTraceIdFilter](#13-logtraceidfilterjava)  
2. [Log Execution Flow](#2-log-usage-method)
3. [Log Environment Configuration](#3-log-main-configuration-applicationyml)  
   - [3.1. Logging Level](#31-logging-level)  
   - [3.2. Log Format](#32-log-format)
4. [Log Usage Examples](#4-log-usage-examples)

---

## 1. **Log Components**

### 1.1. **LogFormatAop.java**
- Manages the **Depth** of service layer or specific method calls and tracks the lifecycle of method calls.
- Uses AOP (Aspect-Oriented Programming) to intercept class and method calls and automatically format log messages.
- Key Features:
    - Method call Depth management (`Depth` addition/removal)
    - Log tracing based on pre/post method call processing using @Around.

---

### 1.2. **LogFormatUtil.java**
- Utility class that manages log format, Depth, and MDC values.
- Key Features:
    - Centralized management of **TraceId** and **Depth** values using MDC (Mapped Diagnostic Context).
    - Provides Depth management functions like `initializeDepth`, `incrementDepth`, `decrementDepth`, `clearDepth`.
    - Configures log formatting based on log levels.

---

### 1.3. **LogTraceIdFilter.java**
- Generates a unique **TraceId** for each request and stores it in MDC.
- Creates **different unique values** for each HTTP request to enable separate tracking of requests and logs.
- Key Roles:
    - Management of `TraceId` values containing unique timestamps
    - Storage and initialization of request-specific TraceId in MDC.

---

## 2. **Log Execution Flow**
1. When an **HTTP request** comes in, `LogTraceIdFilter` executes to generate a unique `TraceId`.
2. `LogFormatAop` tracks Depth and signature at each method call point and constructs log patterns.
3. `LogFormatUtil` manages MDC and Depth-related operations, outputting messages according to the defined log format.
4. Final results are output as **console logs** or **file logs**.
---

## 3. **Log Main Configuration (application.yml)**
- Configures log patterns, log levels, output methods, etc. through YAML files.
- Configuration Elements:
  - **Log Level**: Can set `INFO`, `DEBUG`, `ERROR`, `WARN`, etc., with log level control for specific packages.
  - **Log Pattern**: Specifies the format of log messages output to console and file.
  - **File Logging**: Additional configuration possible for storing logs in files.

### 3.1. **Logging Level**

- Controls the range of logs to output during application execution using log levels.
- Can set global or package-specific log levels in `application.yml`.

```yaml
logging:
  level:
    root: info          # Default log level: INFO
    skcc.arch: debug    # Specific package log level is DEBUG
```

#### Default Log Levels:
- `trace`: Most detailed logs, for development debugging.
- `debug`: Information for debugging.
- `info`: General execution information.
- `warn`: Warning messages.
- `error`: Failures or critical issues.

---

### 3.2. **Log Format**

#### Console Log Pattern
Defines console log format through the `console` area.

```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread][%highlight(%-5level)]%X{traceId}%X{depth} %msg%n"
```

#### Format Components
- `%d{yyyy-MM-dd HH:mm:ss}`: Log output time (e.g., `2025-02-13 15:52:22`)
- `%thread`: Name of the thread that generated the log.
- `%highlight(%-5level)`: Log level display (`INFO`, `DEBUG`, `ERROR`, etc.).
- `%X{traceId}`: Request-specific unique TraceId (managed in MDC).
- `%X{depth}`: Service layer and method Depth Decorate and Signature
- `%msg`: Actual output message.
- `%n`: Line break.

#### File Log Settings (Optional)
Add the following settings to store logs in files.  
For more details, refer to SpringBoot Docs.   
[SpringBoot Logging Docs](https://docs.spring.io/spring-boot/reference/features/logging.html#features.logging.logback-extensions.profile-specific)
```yaml
logging:
  pattern:
# Log file management
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread][%highlight(%-5level)]%X{traceId}%X{depth} %msg%n" # Console log pattern
  file:
    name: logs/app.log # Specify file log path
  logback:
    rollingpolicy:
      file-name-pattern: ${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz
      max-file-size: 10MB
```

---

## 4. **Log Usage Examples**
- Output logs using the lombok annotation @Slf4j.

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
        String msg = "Log 1";
        log.debug("Controller call start: {}", msg);
        logService.logTest();
        log.debug("Controller call end");
    }

    @GetMapping("/2")
    public void getLog2() {
        String msg = "Log 2";
        log.debug("Controller call start: {}", msg);
        logService.logTest2();
        log.debug("Controller call end");
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
        log.debug("Service log call start");
        logRepository.logTest(100);
        log.debug("Repository recall");
        logRepository.logTest(100);
        log.debug("Service log call end");
    }

    public void logTest2() {
        log.debug("Service log call start");
        logRepository.logTest(200);
        log.debug("Service log call end");
    }
}
```

#### Repository
```java
@Repository
@Slf4j
public class LogRepository {

    public void logTest(int number) {
        log.debug("Repository log start");
        log.debug("Repository log: {}", number);
        log.debug("Repository log end");
    }
}
```

---

### Example Output (Console Log)
```
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ LogController.getLog1] Controller call start: Log 1
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ |-->LogService.logTest] Service log call start
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ | |-->LogRepository.logTest] Repository log start
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ | |-->LogRepository.logTest] Repository log: 100
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ | |-->LogRepository.logTest] Repository log end
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ |<--LogService.logTest] Repository recall
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ | |-->LogRepository.logTest] Repository log start
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ | |-->LogRepository.logTest] Repository log: 100
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ | |-->LogRepository.logTest] Repository log end
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ |<--LogService.logTest] Service log call end
2025-02-24 09:13:23 [http-nio-8080-exec-6][DEBUG][1d4c05d0-28ee-4b91-8cb9-4a][ LogController.getLog1] Controller call end
``` 