# HTTP Call Guide
> This document explains how to use the `HttpClient` class, which wraps the WebFlux HTTP client for making HTTP requests in the service.   
> The HTTP methods are primarily composed using polymorphism.  
> I will explain how to use this class through actual code examples.  
> You can customize it according to your project's needs.

---

## Table of Contents

1. [Key Features](#1-key-features)  
   - [1.1 HttpClient Class Function Description](#11-httpclient-class-function-description)  
   - [1.2 Polymorphism Implementation](#12-polymorphism-implementation)

2. [Usage Examples](#2-usage-examples)  
   - [2.1 GET](#21-get)
     - [GET Call Without Query Parameters](#1-get-call-without-query-parameters)
     - [GET Call With Query Parameters](#2-get-call-with-query-parameters)
     - [Call After Setting Header Values](#3-call-after-setting-header-values)
   - [2.2 POST](#22-post)

3. [Additional Notes](#3-additional-notes) 
   - [3.1 HttpOptions Usage](#31-httpoptions-usage) 
---

## 1. Key Features
The provided `HttpClient` class is a utility class designed to handle HTTP requests based on **Spring WebFlux's WebClient**. I will explain how to use this class along with a guide on utilizing polymorphism in the code.

---

### **1.1 HttpClient Class Function Description**
`HttpClient` is designed to provide the following key features:

1. **HTTP Method Support**:
    - Provides various methods to handle HTTP requests like `GET`, `POST`, `PUT`, `DELETE`.

2. **Flexible Option Settings**:
    - Can dynamically set HTTP headers, query parameters, request body, etc.
    - Allows additional custom options through `HttpOptions`.

3. **Response Mapping**:
    - Converts response data to generic type (`responseType`) and returns it, enabling handling of various data responses.

4. **Spring WebFlux Based**:
    - Uses Spring WebFlux's `WebClient` designed asynchronously, implementing high performance and event-based processing.

---

### **1.2 Polymorphism Implementation**
`HttpClient` is designed to utilize various characteristics of polymorphism. Here, I explain the methods and benefits of implementing polymorphism with some key points.

---

#### 1. **Parameterized Method Polymorphism**
Methods like `request`, `get`, `post`, `put`, `delete` can handle various response forms using the generic type `<T>` internally.
For example, even if HTTP request responses come in forms of `String`, `Map`, or user-defined DTO objects, they can be flexibly processed through the same method.

---

#### 2. **Method Overloading**
Methods with the same name (e.g., `get`, `post`) but different parameters are designed to implement different processing methods. This is a form of polymorphism that results in concise calls by providing only the options needed by the user.

```java
public <T> T get(String url, Map<String, Object> queryParams, Object responseType);
public <T> T get(String url, HttpHeaders headers, Map<String, Object> queryParams, Object responseType);
public <T> T get(String url, HttpHeaders headers, Map<String, Object> queryParams, Object responseType, HttpOptions options);
```

The `get` method can be called in various scenarios as shown above.

---

#### 3. **Extensible WebClient Utilization**
Since `HttpClient` is designed to wrap WebClient, new features can be easily added by replacing or extending WebClient.
For example, logging, async/sync call processing, etc., can be implemented by creating separate implementations using polymorphism, delegating, or redefining them.

_Asynchronous call processing is not supported in this guide. (To be updated later)_

## 2. Usage Examples
Samples are written in HttpClientTest.java test code.  
I will explain using the contents of this source.

### 2.1 GET 
1. GET Call Without Query Parameters
    ```java
    @Test
    void GET_call_without_query() throws Exception {
        //given
        String url = "https://jsonplaceholder.typicode.com/todos/1";
    
        //when
        Todo obj = httpClient.get(url, null, Todo.class);
    
        //then
        assertNotNull(obj);
        assertThat(obj.getUserId()).isEqualTo(1);
    }
    ```
    After the HTTP call, you can receive the response object by passing the `Todo.class` class.

2. GET Call With Query Parameters
    ```java
    @Test
    void GET_call_with_query_parameters() throws Exception {
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
    When you pass parameter information, httpClient internally converts it to a query string.  
    In the above example, the response object is defined through `ParameterizedTypeReference`.

3. Call After Setting Header Values
   ```java
   @Test
   void GET_call_with_headers() throws Exception {
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
   You can make calls by adding header values by passing HttpHeader as a parameter.

### 2.2 POST
   ```java
   @Test
   void POST_call() throws Exception {
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
   For POST requests, when you pass an Object in the Body, it is converted to JSON type and sent.

## 3. Additional Notes
### 3.1 HttpOptions Usage
The `HttpOptions` class allows you to specify `retry attempts` and `timeout`.

|  Field   | Default |  Limit   |  Note   |
|:-----:|:---:|:------:|:-----:|
| Retry Attempts |  0  |  Negative   |       |
| Timeout  | 500 | Less than 500 | Unit:ms |

**Example of 3 retry attempts on failure** 
   ```java
   @Test
   void call_with_HttpOptions_settings() throws Exception {
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