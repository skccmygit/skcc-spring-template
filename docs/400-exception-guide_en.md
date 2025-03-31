# Exception Handling Guide
> This guide provides standardization for handling exceptions consistently in the system,
> improving readability and maintainability through custom exceptions and error codes,
> and supporting message parameter binding through Spring's `MessageSource`.

---

## Table of Contents

1. [Exception Handling Components](#exception-handling-components)
   - [1.1 ExceptionDto](#11-exceptiondto)
   - [1.2 ErrorCode (Enum)](#12-errorcode-enum)
   - [1.3 message.properties](#13-messageproperties)
   - [1.4 Custom Exception](#14-custom-exception)
2. [Global Exception Handling](#global-exception-handling)
   - [2.1 Global Exception Handler](#21-global-exception-handler)
   - [2.2 Basic Exception Handling](#22-basic-exception-handling)
3. [Additional Reference Notes](#additional-reference-notes)
   - [3.1 Spring MessageSource and Message Parameter Binding](#31-spring-messagesource-and-message-parameter-binding)

---

## 1. Exception Handling Components

### 1. **`ExceptionDto`**
A standard DTO for transmitting exceptions to clients. 
It includes information such as error codes and messages. It is the object of the `error field` in `ApiResponse`.

```java
package skcc.arch.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionDto {
    private String code;         // Custom error code (code value from ErrorCode[Enum])
    private String message;      // Custom message
}
```

**Note**: ApiResponse has the following structure, and ExceptionDto refers to the object in the error field.
```json
{
    "success": false,
    "data": null,
    "error": {
        "code": 90005,
        "message": "Invalid request. [Field: codeName, Error message: Empty value not allowed]"
    }
}
```

---

### 2. **`ErrorCode` (Enum)**
All error codes used throughout the system are managed as Enum types.  
These are connected to message keys for error messages and are used for multilingual support and dynamic message binding.

Field|Type|Description
:--:|:--:|:--:
code|int|Configure according to project requirements
status|HttpStatus|Use HTTP status codes
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

    // Method to get message dynamically
    public String getMessage(Object... args) {
        if (messageSource == null) {
            throw new IllegalStateException("MessageSource has not been set!");
        }
        // Provide message based on current Locale
        return messageSource.getMessage(messageKey, args, LocaleContextHolder.getLocale());
    }
}
```

### 3. **message.properties** 
Write language-specific message environment files. The content consists of messageKey and messageContents, and provides message parameter functionality.

**`messages.properties` example**:
```properties
error.invalid=Invalid request. Field: {0}, {1}
error.unauthorized=No permission.
error.notFound={0} not found.
```

**`messages_en.properties` example**:
```properties
error.internalServerError=Internal Server Error
error.unauthorized=UnAuthorized.
error.accessDenied=Access Denied.
error.invalidRequest=Invalid Request. [field: {0}, error message: {1}]
```

---

### 4. **Custom Exception**
Exceptions occurring in business logic use **`CustomException`**, and custom exceptions extending it are designed.  
When detailed exceptions are needed, it is recommended to define and use **domain-specific exceptions**.

**`CustomException` example**:
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

## 2. Global Exception Handling 

### **Global Exception Handler**
Uses Spring's `@ControllerAdvice` to handle exceptions globally.  
Converts all exceptions to `ExceptionDto` and provides them in a standardized format in the response.  
Define specific exceptions as needed.

```java
package skcc.arch.app.exception;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Error handling for validation
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final String field = Objects.requireNonNull(e.getBindingResult().getFieldError()).getField();
        final String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        log.error("handleMethodArgumentNotValidException() in GlobalExceptionHandler throw MethodArgumentNotValidException : {}", e.getMessage());

        return ApiResponse.fail(new CustomException(ErrorCode.INVALID_REQUEST,field, message));
    }

    // Exception for non-existent requests
    @ExceptionHandler(value = {NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ApiResponse<?> handleNoPageFoundException(Exception e) {
        log.error("GlobalExceptionHandler catch NoHandlerFoundException : {}", e.getMessage());
        return ApiResponse.fail(new CustomException(ErrorCode.NOT_FOUND_END_POINT));
    }

    // Custom exception
    @ExceptionHandler(value = {CustomException.class})
    public ApiResponse<?> handleCustomException(CustomException e) {
        log.error("handleCustomException() in GlobalExceptionHandler throw CustomException : {}", e.getMessage());
        return ApiResponse.fail(e);
    }

    // Default exception
    @ExceptionHandler(value = {Exception.class})
    public ApiResponse<?> handleException(Exception e) {
        return ApiResponse.fail(e);
    }
}
```

---

## 3. Additional Reference Notes

### **Spring MessageSource and Message Parameter Binding**
Uses Spring's `MessageSource` to dynamically bind error messages based on message keys.
Enables multilingual support. 
The related Config file is `MessageConfig`.

#### 1. `MessageConfig`
Defines message resource location and encoding, and injects message source through `ErrorCode.setMessageSource()`.

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
Parameters can be defined in messages using `{number}`.
```properties
error.invalidRequest=Invalid request. [Field: {0}, Error message: {1}]
```

#### 3. `CustomException`
Configured to receive `message parameter` lists through spread operator.
```java
public CustomException(ErrorCode errorCode, Object... args) {
    super(errorCode.getMessage(args));
    this.errorCode = errorCode;
    this.args = args;
}
```

#### 4. Message Parameter Usage Example
Example of binding message parameters in `GlobalExceptionHandler`:
```java
@ExceptionHandler(value = MethodArgumentNotValidException.class)
public ApiResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    final String field = Objects.requireNonNull(e.getBindingResult().getFieldError()).getField();
    final String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
    log.error("handleMethodArgumentNotValidException() in GlobalExceptionHandler throw MethodArgumentNotValidException : {}", e.getMessage());

    return ApiResponse.fail(new CustomException(ErrorCode.INVALID_REQUEST,field, message));
}
```

`CustomeExceptionTest.java` message parameter binding test code:

```java
@Test
void dynamicMessageParameterBindingTest() throws Exception {
    //given
    LocaleContextHolder.setLocale(Locale.KOREAN);
    String fieldMsg1 = "field1";
    String fieldMsg2 = "field2";
``` 