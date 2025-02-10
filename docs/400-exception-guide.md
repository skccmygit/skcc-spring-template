# 예외 처리 가이드

## 목표
이 가이드는 시스템에서 발생하는 예외를 일관되게 처리하며, 사용자 정의 예외와 에러 코드를 활용해 가독성과 유지보수를 높이고, **Spring의 `MessageSource`를 통해 메시지의 동적 바인딩**을 지원하는 표준화를 제공합니다.

---

## 예외 처리 구성

### 1. **`ExceptionDto`**
발생한 예외를 클라이언트에게 전달하기 위한 표준 DTO입니다.  
이는 에러 코드, 메시지 등의 정보를 포함합니다. ApiResponse의 error 필드의 객체 입니다.

```java
package skcc.arch.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionDto {
    private String code;    // 에러 코드 (ex: INVALID_ERROR)
    private String message;      // 사용자 친화적인 메시지
}
```

---

### 2. **`ErrorCode` (Enum)**
시스템 전체에서 사용되는 모든 에러 코드는 Enum으로 관리합니다.  
이는 에러 메시지의 메시지 키와 연결되며, 다국어 및 동적 메시지 바인딩에 사용됩니다.

```java
package skcc.arch.app.exception;

import lombok.Getter;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_ERROR("error.invalid"),
    UNAUTHORIZED("error.unauthorized"),
    NOT_FOUND("error.notFound");

    private final Integer code;
    private final HttpStatus status;
    private final String messageKey;

    ErrorCode(String code) {
        this.code = code;
    }


    @Setter
    private static MessageSource messageSource;

    // 메시지를 동적으로 가져오는 메서드 추가
    public String getMessage(Object... args) {
        if (messageSource == null) {
            throw new IllegalStateException("MessageSource has not been set!");
        }
        // 현재 Locale을 기반으로 메시지 제공
        return messageSource.getMessage(messageKey, args, LocaleContextHolder.getLocale());
    }
}
```

**`messages.properties` 예시**:
```properties
error.invalid=유효하지 않은 요청입니다. 필드: {0}, {1}
error.unauthorized=권한이 없습니다.
error.notFound={0}를 찾을 수 없습니다.
```

---

### 3. **사용자 정의 예외**
비즈니스 로직에서 발생하는 시스템 예외를 표현하기 위해 **`CustomException`** 및 이를 확장한 사용자 정의 예외를 설계합니다.  
상세예외가 필요할경우 도메인별 예외를 정의하여 사용하는것을 권장합니다.

#### 사용자 정의 예외 (예: `CustomException`):
```java
@Getter
public class CustomException extends RuntimeException{
    public final ErrorCode errorCode;
    public final Object[] args;

    public CustomException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage(args));
        this.errorCode = errorCode;
        this.args = args;
    }

    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.args = null;
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage(this.args);
    }
}

```

---

### 4. **Global Exception Handler**
스프링의 `@ControllerAdvice`를 활용하여 발생한 예외를 전역적으로 처리합니다.  
모든 예외를 `ExceptionDto`로 변환하여 응답에 표준화된 형태로 제공합니다.  
필요에 따라 특정 예외를 정의하도록 합니다.

```java
package skcc.arch.app.exception;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 유효값에 대한 에러 처리
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final String field = Objects.requireNonNull(e.getBindingResult().getFieldError()).getField();
        final String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        log.error("handleMethodArgumentNotValidException() in GlobalExceptionHandler throw MethodArgumentNotValidException : {}", e.getMessage());

        return ApiResponse.fail(new CustomException(ErrorCode.INVALID_REQUEST,field, message));
    }

    // 존재하지 않는 요청에 대한 예외
    @ExceptionHandler(value = {NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ApiResponse<?> handleNoPageFoundException(Exception e) {
        log.error("GlobalExceptionHandler catch NoHandlerFoundException : {}", e.getMessage());
        return ApiResponse.fail(new CustomException(ErrorCode.NOT_FOUND_END_POINT));
    }

    // 커스텀 예외
    @ExceptionHandler(value = {CustomException.class})
    public ApiResponse<?> handleCustomException(CustomException e) {
        log.error("handleCustomException() in GlobalExceptionHandler throw CustomException : {}", e.getMessage());
        return ApiResponse.fail(e);
    }

    // 기본 예외
    @ExceptionHandler(value = {Exception.class})
    public ApiResponse<?> handleException(Exception e) {
        return ApiResponse.fail(e);
    }
}
```

---

### 5. **Spring MessageSource 및 메시지 바인딩**
스프링의 `MessageSource`를 사용하여 메시지 키를 기반으로 에러 메시지를 동적으로 바인딩하며, 다국어 지원을 가능하게 만듭니다.  
관련 Config 파일은 `MessageConfig` 입니다.

#### `MessageConfig.java`
```java
@Configuration
public class MessageConfig {

    @PostConstruct
    public void init() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        ErrorCode.setMessageSource(messageSource);
    }
}
```

#### 메시지 동적 바인딩 예시:
`GlobalExceptionHandler`에서 메시지를 동적으로 바인딩하는 코드:
```java
@ExceptionHandler(value = MethodArgumentNotValidException.class)

public ApiResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    final String field = Objects.requireNonNull(e.getBindingResult().getFieldError()).getField();
    final String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
    log.error("handleMethodArgumentNotValidException() in GlobalExceptionHandler throw MethodArgumentNotValidException : {}", e.getMessage());

    return ApiResponse.fail(new CustomException(ErrorCode.INVALID_REQUEST,field, message));
}
```
CustomException의 전개연산자를 통하여 파라미터를 받도록 구성하였습니다.
```java
public CustomException(ErrorCode errorCode, Object... args) {
    super(errorCode.getMessage(args));
    this.errorCode = errorCode;
    this.args = args;
}
```

**`messages.properties`에서 `error.invalid` 메시지 예시**:
```properties
error.invalidRequest=유효하지 않은 요청입니다. [필드: {0}, 오류 메시지: {1}]
```

---

### 6. CustomException 활용 예시