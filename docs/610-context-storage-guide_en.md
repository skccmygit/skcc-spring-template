# Context Storage Usage Guide
> This project uses JWT Token-based authentication without sessions (stateless policy).       
> However, in certain situations, there is a need to store, read, and delete data for each user. 
> 
> Context Storage is a repository designed to maintain data required for business requests to address these needs.    
> It is designed to manage user-specific data by extracting the user's unique ID (`uid`) from the `Security Context`.

---

## **Key Features**
### 1. **Data Storage (`set`)**
- Stores data in a specific user's repository.
- If a key already exists, it will be replaced with the new value.
    ```java
    /**
     * Stores data. Saves key and value for the uid, overwriting if the key exists.
     *
     * @param key  Key of the data to store
     * @param value Data value to store (Object)
     */
    void set(String key, Object value);
    ```

### 2. **Data Retrieval (`get`)**
- Retrieves data for a specific key from the user's repository.
- Returns an empty value if the data doesn't exist or has a different type.

    ```java
    /**
     * Retrieves data.
     *
     * @param key  Key of the data to find
     * @param type Data type to return
     * @param <T>  Return data type
     * @return Value matching the key, null if not found
     */
    <T> T get(String key, Class<T> type);
    ```

### 3. **Data Deletion (`remove`)**
- Deletes a specific key from the user's repository.

    ```java
    /**
     * Deletes data.
     *
     * @param key Key of the data to delete
     */
    void remove(String key);
    ```

### 4. **Complete Data Deletion (`clear`)**
- Deletes all data for a specific user.

    ```java
    /**
     * Deletes all data associated with the uid.
     */
    void clear();
    ```
  
---
## **Usage Examples**
> Refer to CaffeineContextStorageServiceTest and ContextController files

- set (Storing with specific DTO)
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
- get (Retrieving with specified type)
  ```java
  @Test
  void testGetWithInvalidKey() {
      String uid = "user123";
      when(authUtil.getUID()).thenReturn(uid);
  
      TestDTO result = contextStorageService.get("nonExistentKey", TestDTO.class);
  
      assertNull(result);
  }
  ```
- Usage in actual code
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
## **Reference Notes**
- It may not work for **calls during authentication exceptions**. 
- Currently operates based on Caffeine cache, but can be **extended** to various storage bases (e.g., Redis) in the future. 