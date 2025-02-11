# 예외 처리 가이드
> 이 가이드는 시스템에서 발생하는 예외를 일관되게 처리하며, 
> 사용자 정의 예외와 에러 코드를 활용해 가독성과 유지보수를 높이고, 
> **Spring의 `MessageSource`를 통해 메시지의 파라미터 바인딩**을 지원하는 표준화를 제공합니다.

---
## 목차

1. [예외 처리 구성 요소](#예외-처리-구성-요소)
   - [1.1 ExceptionDto](#11-exceptiondto)
   - [1.2 ErrorCode (Enum)](#12-errorcode-enum)
   - [1.3 message.properties](#13-messageproperties)
   - [1.4 사용자 정의 예외 CustomException](#14-사용자-정의-예외-customexception)
2. [전역 예외 처리](#전역-예외-처리)
   - [2.1 Global Exception Handler](#21-global-exception-handler)
   - [2.2 기본 예외 처리](#22-기본-예외-처리)
3. [기타 참고사항](#기타-참고사항)
   -  [3.1 Spring MessageSource 및 메시지 파라미터 바인딩](#31-spring-messagesource-및-메시지-파라미터-바인딩)
---

## 1. 예외 처리 구성 요소

### 1. **`ExceptionDto`**
발생한 예외를 클라이언트에게 전달하기 위한 표준 DTO입니다. 
이는 에러 코드, 메시지 등의 정보를 포함합니다. `ApiResponse`의 `error 필드`의 객체 입니다.

```java
package skcc.arch.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionDto {
    private String code;         // 사용자 정의 에러코드 (ErrorCode[Enum]의 코드 값)
    private String message;      // 사용자 정의 메시지
}
```

**주의사항**: ApiResponse는 아래 형태이며 ExceptionDto의 경우 error 필드의 객체를 뜻 합니다.
```json
{
    "success": false,
    "data": null,
    "error": {
        "code": 90005,
        "message": "유효하지 않은 요청입니다. [필드: codeName, 오류 메시지: 빈 값을 허용하지 않습니다]"
    }
}
```
---

### 2. **`ErrorCode` (Enum)**
시스템 전체에서 사용되는 모든 에러 코드는 Enum 타입으로 관리합니다.  
이는 에러 메시지의 메시지 키와 연결되며, 다국어 및 동적 메시지 바인딩에 사용됩니다.

필드|타입|내용
:--:|:--:|:--:
code|int|프로젝트에 맞게 구성 
status|HttpStatus|Http 상태코드 사용
messageKey|String|Message.property key


```java
package skcc.arch.app.exception;

import lombok.Getter;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_FOUND_ELEMENT(10001, HttpStatus.NOT_FOUND, "error.notFoundElement"),
    EXIST_ELEMENT(10002, HttpStatus.BAD_REQUEST, "error.existElement"),
    NOT_MATCHED_PASSWORD(10003, HttpStatus.BAD_REQUEST, "error.notMatchedPassword"),
    NOT_FOUND_FILE(10004, HttpStatus.NOT_FOUND, "error.notFoundFile"),
    NOT_FOUND_END_POINT(90001, HttpStatus.NOT_FOUND, "error.notFoundEndPoint"),
    INTERNAL_SERVER_ERROR(90002, HttpStatus.INTERNAL_SERVER_ERROR, "error.internalServerError"),
    UNAUTHORIZED(90003, HttpStatus.UNAUTHORIZED, "error.unauthorized"),
    ACCESS_DENIED(90004, HttpStatus.FORBIDDEN, "error.accessDenied"),
    INVALID_REQUEST(90005, HttpStatus.BAD_REQUEST, "error.invalidRequest");

    private final int code;
    private final HttpStatus status;
    private final String messageKey;

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

### 3. **message.properties** 
언어별 message 환경파일을 작성한다. 내용은 messageKey, messageContents로 구성되며 메시지 파라미터 기능을 제공합니다.

**`messages.properties` 예시**:
```properties
error.invalid=유효하지 않은 요청입니다. 필드: {0}, {1}
error.unauthorized=권한이 없습니다.
error.notFound={0}를 찾을 수 없습니다.
```

**`messages_en.properties` 예시**:
```properties
error.internalServerError=Internal Server Error
error.unauthorized=UnAuthorized.
error.accessDenied=Access Denied.
error.invalidRequest=Invalid Request. [field: {0}, error message: {1}]
```
---

### 4. **사용자 정의 예외**
비즈니스 로직에서 발생하는 예외는 **`CustomException`** 을 사용하며, 이를 확장한 사용자 정의 예외를 설계합니다.  
상세 예외가 필요할경우 **도메인별 예외**를 정의하여 사용하는것을 권장합니다.

**`CustomException` 예시**:
```java
@Getter
public class CustomException extends RuntimeException{
    public final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage(args));
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

```
---

## 2. 전역 예외 처리 

### **Global Exception Handler**
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

## 3. 기타 참고사항

### **Spring MessageSource 및 메시지 파라미터 바인딩**
스프링의 `MessageSource`를 사용하여 메시지 키 기반으로 에러 메시지를 동적으로 바인딩합니다.
다국어 지원을 가능하게 만듭니다. 
관련 Config 파일은 `MessageConfig` 입니다.

#### 1. `MessageConfig`
메세지 리소스 위치 및 인코딩을 정의하고 `ErrorCode.setMessageSource()` 를 통해 메시지소스를 주입합니다.

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

#### 2. `message.properties`
`중괄호{숫자}`를 통하여 메시지에 파라미터를 정의할 수 있습니다. 
```properties
error.invalidRequest=유효하지 않은 요청입니다. [필드: {0}, 오류 메시지: {1}]
```

#### 3. `CustomException은`
`전개연산자`를 통하여 `메세지 파라미터` 목록을 받을 수 있도록 구성하였습니다.
```java
public CustomException(ErrorCode errorCode, Object... args) {
    super(errorCode.getMessage(args));
    this.errorCode = errorCode;
    this.args = args;
}
```
#### 4. 메시지 파라미터 사용 예시
`GlobalExceptionHandler`에서 메시지 파라미터를 바인딩하는 예제:
```java
@ExceptionHandler(value = MethodArgumentNotValidException.class)

public ApiResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    final String field = Objects.requireNonNull(e.getBindingResult().getFieldError()).getField();
    final String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
    log.error("handleMethodArgumentNotValidException() in GlobalExceptionHandler throw MethodArgumentNotValidException : {}", e.getMessage());

    return ApiResponse.fail(new CustomException(ErrorCode.INVALID_REQUEST,field, message));
}
```

`CustomeExceptionTest.java` 메시지 파라미터 바인딩 테스트 코드 :

```java
@Test
void 동적메시지_파라미터_바인딩_정상확인() throws Exception {
        //given
        LocaleContextHolder.setLocale(Locale.KOREAN);
        String fieldMsg1 = "field1";
        String fieldMsg2 = "field2";
        String finalMsg = String.format("유효하지 않은 요청입니다. [필드: %s, 오류 메시지: %s]", fieldMsg1, fieldMsg2);

        //when
        CustomException exception = new CustomException(ErrorCode.INVALID_REQUEST, fieldMsg1, fieldMsg2);

        //then
        String exceptionMessage = exception.getMessage();
        assertEquals(finalMsg, exceptionMessage);
        assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());
        
}
```
