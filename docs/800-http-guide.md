# Http 호출 가이드 guide
> 이 문서는 서비스에서 Http 요청이 필요할때 weflux httpclient를 Wrapping 한 클래스인 `HttpClient` 클래스를 사용하는 방법에 대하여 설명합니다.   
> 주로 Http Method를 다형성 구성하였습니다.  
> 실제 코드를 통하여 해당 클래스를 사용하는 방법에 대하여 설명하겠습니다.  
> 추가로 프로젝트 성격에 맞게 커스터마이징 하여 사용하시면 됩니다.
---

## 목차

1. [주요기능](#1-주요기능)  
   - [1.1 HttpClient 클래스 기능 설명](#11-httpclient-클래스-기능-설명)  
   - [1.2 다형성 적용](#12-다형성-적용)

2. [사용예제](#2-사용예제)  
   - [2.1 GET](#21-get)
     - [쿼리파라미터가 없는 GET 호출](#1-쿼리파라미터가-없는-get-호출)
     - [쿼리파라미터가 존재하는 GET 호출](#2-쿼리파라미터가-존재하는-get-호출)
     - [Header 값 세팅 후 호출](#3-header-값-세팅-후-호출)
   - [2.2 POST](#22-post)

3. [기타사항](#3-기타사항) 
   - [3.1 HttpOption 사용](#31-httpoptions-사용) 
---

## 1. 주요기능
제공된 `HttpClient` 클래스는 **Spring WebFlux의 WebClient**를 기반으로 HTTP 요청을 처리하도록 설계된 유틸리티 클래스입니다. 코드를 바탕으로 다형성을 활용하는 가이드와 함께, 이 클래스의 사용 방법을 설명하겠습니다.

---

### **1.1 HttpClient 클래스 기능 설명**
`HttpClient`는 다음과 같은 주요 기능을 제공하도록 설계되어 있습니다:
1. **HTTP 메서드 지원**:
    - `GET`, `POST`, `PUT`, `DELETE` 와 같은 HTTP 요청을 처리하는 다양한 메서드를 제공합니다.

2. **유연한 옵션 설정**:
    - HTTP 헤더, 쿼리 파라미터, 요청 바디 등을 동적으로 설정할 수 있습니다.
    - `HttpOptions`를 이용해 커스텀 옵션을 추가적으로 설정 가능.

3. **응답 매핑**:
    - 요청의 응답 데이터를 제네릭 타입(`responseType`)으로 변환하여 반환합니다. 이를 통해 다양한 데이터 응답을 처리할 수 있습니다.

4. **Spring WebFlux 기반**:
    - 비동기적으로 설계된 Spring WebFlux의 `WebClient`를 사용하며, 이를 통해 높은 성능과 이벤트 기반 처리로 구현되어 있습니다.

---

### **1.2 다형성 적용**
`HttpClient`는 다형성의 여러 특징을 활용한 방식으로 설계되었습니다. 여기에서 몇 가지 키포인트로 다형성을 적용하는 방법과 이점을 설명합니다.

---
#### 1. **파라미터화된 메서드 다형성**
`request`, `get`, `post`, `put`, `delete` 등의 메서드 내부에서 제네릭 타입 `<T>`를 사용하여 다양한 응답 형태를 처리할 수 있습니다.
예를 들어, HTTP 요청의 응답이 `String`, `Map`, 또는 사용자가 정의한 DTO 객체 형태로 들어오더라도 같은 메서드를 통해 유연하게 처리할 수 있습니다.
---
#### 2. **Method Overloading(메서드 오버로딩)**
같은 이름의 메서드(예: `get`, `post`)지만 서로 다른 파라미터를 사용하여 처리 방식을 다르게 구현될 수 있도록 설계되어 있습니다. 이는 다형성의 한 형태로, 사용자가 필요한 옵션만 제공하여 간결한 호출을 초래합니다.
``` java
public <T> T get(String url, Map<String, Object> queryParams, Object responseType);
public <T> T get(String url, HttpHeaders headers, Map<String, Object> queryParams, Object responseType);
public <T> T get(String url, HttpHeaders headers, Map<String, Object> queryParams, Object responseType, HttpOptions options);
```
위와 같은 방식으로 `get` 메서드는 다양한 시나리오에서 호출할 수 있습니다.

---

#### 3. **확장 가능한 WebClient 활용**
`HttpClient`가 WebClient를 감싸는 방식으로 설계되었기 때문에, WebClient를 대체하거나 확장하는 방식으로 손쉽게 새로운 기능을 추가할 수 있습니다.
예를 들어, 로깅, 비동기/동기 호출 처리 등은 다형성을 활용하여 별도의 구현체를 만들고 위임하거나 재정의할 수 있습니다. 

_본 가이드에서는 비동기 호출 처리는 지원되지 않습니다.(추후 업데이트)_


## 2. 사용예제
HttpClientTest.java 테스트코드로 샘플을 작성하였습니다.  
해당 소스의 내용을 가지고 설명하도록 하겠습니다.

### 2.1 GET 
1. `쿼리파라미터가` 없는 GET 호출
    ```java
    @Test
    void GET_쿼리_없는_호출() throws Exception {
        //given
        String url = "https://jsonplaceholder.typicode.com/todos/1";
    
        //when
        Todo obj = httpClient.get(url, null, Todo.class);
    
        //then
        assertNotNull(obj);
        assertThat(obj.getUserId()).isEqualTo(1);
    }
    ```
    HTTP 호출 후, 응답 객체를 `Todo.class` 클래스를 넘겨주어 받을 수 있습니다.   


2. `쿼리파라미터가` 존재하는 GET 호출  
    ```java
    @Test
    void GET_쿼리파라미터_있는_호출() throws Exception {
        //given
        String url = "https://jsonplaceholder.typicode.com/todos";
        Map<String, Object> params = Map.of("userId", 1);
    
        //when
        ParameterizedTypeReference<List<Todo>> responseType = new ParameterizedTypeReference<>() {};
        List<Todo> list = httpClient.get(url, params, responseType);
    
        //then
        assertNotNull(list);
        assertThat(list).hasSizeGreaterThan(0);
        assertThat(list.get(0).getUserId()).isEqualTo(1);
    }
    ```
   파라미터 정보를 넘겨주면 httpClient 내부에서 쿼리스트링으로 변환해줍니다.  
   위 예제에서는 `ParameterizedTypeReference`를 통하여 응답객체를 정의하였습니다.


3. `Header` 값 세팅 후 호출
   ```java
   @Test
       void GET_헤더포함하여_호출() throws Exception {
           //given
           String url = "https://jsonplaceholder.typicode.com/todos";
           Map<String, Object> params = Map.of("userId", 1);
   
           HttpHeaders headers = new HttpHeaders();
           headers.add("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsInVpZCI6InRlc3RAZ21haWwuY29tIiwicm9sZSI6IlVTRVIiLCJlbWFpbCI6InRlc3RAZ21haWwuY29tIiwidXNlcm5hbWUiOiLthYzsiqTtirgiLCJpYXQiOjE3Mzk1MDYxMzEsImV4cCI6MTczOTUwNzkzMX0.bt_XWKvYigydSj69RAKals6fhU9aaKdRbjTLZbDYgMI");
   
           //when
           ParameterizedTypeReference<List<Todo>> responseType = new ParameterizedTypeReference<>() {};
           List<Todo> list = httpClient.get(url, headers, params, responseType);
   
           //then
           assertNotNull(list);
           assertThat(list).hasSizeGreaterThan(0);
           assertThat(list.get(0).getUserId()).isEqualTo(1);
   
       }
   ```
   HttpHeader 를 파라미터로 넘겨 헤더값을 추가하여 호출 할 수 있습니다.


### 2.2 POST
   ```java
   @Test
    void POST_호출() throws Exception {
        //given
        Post requestData = Post.builder()
                .title("test")
                .body("test")
                .userId(1)
                .build();
   
        //when
        Post resultData = httpClient.post("https://jsonplaceholder.typicode.com/posts", requestData, Post.class);
   
        //then
        assertThat(resultData.getId()).isEqualTo(101L);
        assertThat(resultData.getTitle()).isEqualTo(requestData.getTitle());
        assertThat(resultData.getBody()).isEqualTo(requestData.getBody());
        assertThat(resultData.getUserId()).isEqualTo(requestData.getUserId());
    }
   ```
   POST 요청의 경우 Body에 Object 객체를 던지면 JSON 타입으로 변환하여 요청 됩니다.

## 3. 기타사항
### 3.1 HttpOptions 사용
`HttpOptions` 클래스의 경우 `재시도횟수`와 `타임아웃`을 지정할 수 있습니다.

|  필드   | 기본값 |  제한값   |  비고   |
|:-----:|:---:|:------:|:-----:|
| 재시도횟수 |  0  |   음수   |       |
| 타임아웃  | 500 | 500 미만 | 단위:ms |

**실패시 3번 요청 시도 예시 입니다** 
   ```java
    @Test
    void HttpOptions값_세팅하여_호출() throws Exception {
        //given
        String url = "http://localhost:8080/api/users/1";
        HttpOptions httpOptions = HttpOptions.builder()
                .retryAttempts(3)
                .timeout(3000)
        .build();
   
        //when
        Map result = httpClient.get(url, null, Map.class, httpOptions);
   
        //then
        assertNotNull(result);
        
    }
   ```
