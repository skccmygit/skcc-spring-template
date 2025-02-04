
# 개요
## 목적
프로젝트 개발을 위한 표준을 제시하는데 목적이 있습니다

## 범위
본 문서는 목표 시스템 구축을 위한 신규 개발되는 시스템을 범위로 하며 솔루션 및 라이브러리는 대상에서 제외됩니다.

# Naming Rule 가이드
## Naming 표기법
### 기본 Naming Rule
- 이해가 가능한 Full English Description 방식을 사용합니다.
- 독립어나 두 단어가 조합될 경우 두 번째 명사의 시작 문자는 대문자로 시작합니다.
- 이미 잘 알려진 이니셜 단어는 대문자로 구성합니다.
- 정의되는 이름의 길이는 최대 30자 이하로 사용합니다 ( e.g. 클래스, 인터페이스, 메서드, 변수, 상수, 파일 명)
- 예약어를 사용하지 않는다.
- 상수는 영문 대문자 스네이크 표기법을 사용한다. ex) CONTENT_MANAGEMENT
- 변수, 함수에는 카멜 표기법을 사용한다.
- URL, HTML 같은 범용적인 대문자 약어는 대문자 그대로 사용한다

## Java Programming Naming Rule
### Package
> Package 명은 반드시 `소문자`만 사용합니다.  
> Package를 정의할 때는 레벨화하여 명명함을 기본으로 합니다.  
> 각 레벨에 올 수 있는 단어는 2~15자 내외의 영문 소문자를 사용합니다.

### Package 레벨 구조

Level| 명칭            |설명
--|---------------|--
1| app           |Application 기본
2| aop           |AOP 영역
2| config        |Spring Config 영역
2| dto           |Application 영역 DTO
2| exception     |예외
2| file          |파일처리
2| filter        |Servelet Filter
2| handler       |Handler
2| resolver      |resolver
2| util          |각종 유틸
1| common        |공통 도메인
1| business      |업무영역
2| controller    |컨트롤러(웹)
2| domain        |**도메인(모델)**
2| service       |서비스 로직
2| infrastructure |실제구현방법
3| jpa | JPA 구현시 
3| mybatis | MyBatis 구현시 
3| port | 계층간 연결 Interface 존재

### Method
> 일반적으로 Method는 클래스의 여러 가지 행위를 나타내는 것이기 때문에 `첫 단어는 동사`로 시작합니다.  
> 동사만으로 의미 전달이 불명확한 경우에는 `동사 + 명사` 형태로 표기합니다.  
> `CamelCase`로 작성합니다.

Method명칭|설명
--|--
getCode|Code Entity 상세조회
getCodeList|Code Entity 목록 조회 
getTotalCount|목록 전체 총 건수
validMethod|특정항목 유효성 검사
isMethod|특정 속성 여부 검사
hasMethod|특정 속성 소유 검사

### 변수
> 변수 이름을 정의할 때는 약어 사용은 자제하며, 되도록 변수명만 보고 의미를 알 수 있도록 단어 그대로 사용하는 것을 권장합니다.  
> CamelCase로 작성합니다.  
> 첫 글자는 밑줄(_)이나 달러 문자로 ($) 시작하지 않습니다.

### 상수 Constant
> 상수 선언은 반드시 "static final"을 사용합니다.  
> 상수는 전부 대문자 스네이크 표기법으로 표기합니다.  
> 단어와 단어 사이는 밑줄로 (_) 연결합니다.  
> 첫 글자는 밑줄(_)이나 달러 문자로 ($) 시작하지 않습니다.  
> 누구나 이해할 수 있는 영문 이름을 사용합니다.  

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
```


---

## 명명규칙
- CamelCase로 작성
- get+명사+(List)
- validate+주체

## 예외처리
- 기본적인 가이드는 Service 계층에서 발생

https://chamomile.lotteinnovate.com/guides/%EB%B0%B1%EC%97%94%EB%93%9C-%EA%B0%9C%EB%B0%9C-%ED%91%9C%EC%A4%80-%EA%B0%80%EC%9D%B4%EB%93%9C/