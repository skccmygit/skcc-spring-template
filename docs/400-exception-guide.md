### 예외처리

#### 예외 핸들러 (Exception Handler)

1. @ExceptionHandler 사용하여 유형별로 예외처리에 대응할 수 있습니다.
2. ControllerExceptionHandler: @RestController에서 예외가 발생할 때 공통으로 예외를 처리하는 클래스입니다.
3. @RestControllerAdvice 어노테이션을 사용하여 @RestController에서 발생하는 예외를 처리합니다.
4. @ExceptionHandler 어노테이션을 통해 예외 유형별로 컨트롤할 수 있습니다.

#### 공통 Exception Handler의 사용

> @RestControllerAdvice를 사용하면 Spring에서 예외가 발생할 때 자동으로 관리합니다.
>
GlobalExceptionHandler.java 예시

```java 


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