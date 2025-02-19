# 컨텍스트 저장소 사용 가이드
> 본 프로젝트는 JWT Token 기반으로 세션을 사용하지 않습니다. (stateless 정책)       
> 하지만, 특정 상황에서는 사용자별로 데이터를 저장, 읽기, 삭제하는 기능이 필요합니다. 
> 
> 컨텍스트 저장소는 이러한 문제점을 해결하기 위하여 비즈니스 요청에 필요한 데이터를 유지하는 저장소 입니다.    
> 사용자 고유 ID(`uid`)를 `Security Context`에서 추출하여 사용자별 데이터를 관리할 수 있도록 설계되었습니다.

---

## **주요 기능**
### 1. **데이터 저장 (`set`)**
- 특정 사용자의 저장소에 데이터를 저장합니다.
- 이전에 동일한 키가 존재할 경우, 새 값으로 대체됩니다.
    ```java
    /**
     * 데이터를 저장합니다. uid에 대해 key와 value를 저장하며, key가 존재할 경우 덮어씁니다.
     *
     * @param key  저장할 데이터의 키
     * @param value 저장할 데이터 값 (Object)
     */
    void set(String key, Object value);

    ```
### 2. **데이터 읽기 (`get`)**
- 사용자의 저장소에서 특정 키에 대한 데이터를 가져옵니다.
- 데이터가 없거나 타입이 다르면 빈 값을 반환합니다.

    ```java
    /**
     * 데이터를 가져옵니다.
     *
     * @param key  찾고자 하는 데이터의 키
     * @param type 반환할 데이터 타입
     * @param <T>  반환 데이터 타입
     * @return key로 매칭된 값, 없을 경우 null
     */
    <T> T get(String key, Class<T> type);
    ```

### 3. **데이터 삭제 (`remove`)**
- 특정 사용자의 저장소에서 특정 키를 삭제합니다.

    ```java
    /**
     * 데이터를 삭제합니다.
     *
     * @param key 삭제할 데이터의 키
     */
    void remove(String key);
    ```

### 4. **전체 데이터 삭제 (`clear`)**
- 특정 사용자의 모든 데이터를 삭제합니다.

    ```java
    /**
     * uid와 연관된 모든 데이터를 삭제합니다.
     *
     */
    void clear();
    ```
  
---
## **사용예제**
> CaffeineContextStorageServiceTest 및 ContextController 파일 참고

- set (특정 DTO로 저장)
  ```java
  @Test
  void testSetAndGetWithDTO() {
      String uid = "user123";
      when(authUtil.getUID()).thenReturn(uid);
  
      TestDTO dto = new TestDTO("testValue");
  
      contextStorageService.set("dtoKey", dto);
      TestDTO retrieved = contextStorageService.get("dtoKey", TestDTO.class);
  
      assertNotNull(retrieved);
      assertEquals(dto.getValue(), retrieved.getValue());
  }
  ```
- get (명시한 타입으로 가져오기)
  ```java
  @Test
  void testGetWithInvalidKey() {
      String uid = "user123";
      when(authUtil.getUID()).thenReturn(uid);
  
      TestDTO result = contextStorageService.get("nonExistentKey", TestDTO.class);
  
      assertNull(result);
  }
  ```
- 실제 코드에서 사용
  ```java
  @RestController
  @RequestMapping("/api/context")
  @RequiredArgsConstructor
  public class ContextController {
  
      private final ContextStorageService ctxService;
  
      @PostMapping(value = "/set/{key}")
      public String setContext(@PathVariable String key, @RequestBody Map<String, Object> data) {
          ctxService.set(key, data);
          return "ctx-set-ok";
      }
  
  
      @GetMapping(value = "/get/{key}")
      public ApiResponse<?> getContext(@PathVariable String key) {
          return ApiResponse.ok(ctxService.get(key, Map.class));
      }
  }
  ```




---
## 참고사항
- **인증 예외 대상 호출**인 경우에는 동작할 수 없습니다. 
- 현재 Caffeine 캐시 기반으로 동작하나 추후 다양한 저장소 기반(예:Redis)으로 **확장**할 수 있습니다.