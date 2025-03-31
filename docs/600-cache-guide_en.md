# Cache Usage Guide
> Provides direction for implementing and utilizing cache functionality.  
> Cache implementation is done according to the project's requirements using appropriate `cache technology`.  
> Covers cache type selection through environment file (yml) settings, dependency management, and main service class usage.  
> This example uses `Caffeine` and `Redis` caches.

## Table of Contents
1. [Cache Components](#1-cache-components)
   - [1.1 Package Structure](#11-package-structure)
   - [1.2 Directory Example](#12-directory-example)
   - [1.3 CacheService Interface](#13-cacheservice-interface)
   - [1.4 Custom Cache Service (MyCacheService)](#14-custom-cache-service-mycacheservice)
       - [1.4.1 CacheGroup (enum)](#141-component-cachegroup-enum)
       - [1.4.2 MyCacheService](#142-mycacheservice)
       - [1.4.3 CacheController](#143-cachecontroller)
2. [Configuration Settings](#2-cache-configuration-springconfig-and-applicationyml)
   - [2.1 CacheConfig](#21-cacheconfig)
   - [2.2 application-{profile}.yml](#22-application-profileyml)
3. [Cache Usage Examples](#3-cache-usage-examples)
4. [Reference Notes (Relationships between Implementations)](#4-reference-notes-relationships-between-implementations)

---

## 1. Cache Components

### 1.1 Package Structure
Cache-related functionality is organized by package as follows:

- **`skcc.arch.app.cache`**:
    - Defines cache interfaces and actual cache implementations.   
    - Dependencies are injected through CacheConfig.
- **`skcc.arch.biz.common`**:
    - Separately configures and uses cache services for the project. Example: MyCacheService

### 1.2 Directory Example
src/    
├── skcc/arch/app/cache   
│ ├── CacheConfig.java  
│ ├── CacheService.java  
│ ├── CaffeineCacheService.java  
│ └── RedisCacheService.java  
└── skcc/arch/biz/common  
│ ├── constatns/CacheGroup.java   
│ ├── service/MyCacheService.java  
│ ├── controller/CacheController.java  
│ └── ...

### 1.3 CacheService Interface
The top-level cache interface is defined as follows.  
Implementations based on `cache technology` inherit and implement this interface.  
This guide uses `CaffeineCache` for local cache and `RedisCache` for server cache technologies.

```java
public interface CacheService {
    <T> T get(String key, Class<T> type);
    void put(String key, Object value);
    void evict(String key);
    void clearAll();
    void clearByCacheGroup(String cacheGroupName);
}
```

### 1.4 Custom Cache Service (MyCacheService)
Cache services can be customized according to project needs. This guide uses MyCacheService.
`MyCacheService` adds `CacheGroup` as a logical group and `initial loading functionality`.

#### 1.4.1 Component: `CacheGroup (enum)` 
Defines cache groups to be used. Cache groups must be defined to prevent indiscriminate cache usage.  
A cache group can have multiple cache keys.
- **Usage Example**:
    ```java
      myCacheService.get(CacheGroup.CODE, condition.getCode(), object type);
      myCacheService.put(CacheGroup.CODE, object);
    ```

#### 1.4.2 `MyCacheService` 
Defines `initial loading, storage, deletion, initialization, cache name-based initialization, and cache delimiter`.
Implementation can be either Redis or Caffeine cache.

##### Key Methods
```java
public void loadCacheData();
public <T> T get(CacheGroup cacheGroup, String key, Class<T> clazz);
public void put(CacheGroup cacheGroup, String key, Object value);
public void evict(CacheGroup cacheGroup, String key);
public void clearAll();
public void clearByCacheGroup(CacheGroup cacheGroup);
```

##### Operations
- `loadCacheData`: Defines cache to be loaded initially. (Mainly used in memory-based cache)
- `get`: Retrieves data for a specific key value based on cache group. (Must match the type of object stored in cache)
- `put`: Stores key and data in cache group
- `evict`: Deletes data for a specific key in cache group
- `clearAll`: Clears all data in cache
- `clearByCacheGroup`: Clears all data in a specific cache group

##### Sample Example
```java
@RequiredArgsConstructor
@Slf4j
public class MyCacheService {

    public static final String DELIMITER = ":";
    private final CacheService cacheService;
    private final CodeRepository codeRepository;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void loadCacheData() {

        // Memory (Local)
        if(cacheService instanceof CaffeineCacheService) {
            // Cache to be loaded initially
            loadCodeCacheData();
            log.info("Cache loading completed");
        }
        // Redis (Server)
        else if (cacheService instanceof RedisCacheService) {
            // Is initial loading needed?
            loadCodeCacheData();
        }
    }

    /**
     * Design cache according to business requirements
     * Sample - Only loads top-level parents of code domain into cache.
     *       CacheGroup : CacheGroup.CODE
     *       KEY: Parent's code value (based on parentCodeId)
     *       VALUE: Code model (including lowest level elements)
     */
    private void loadCodeCacheData() {
        // Query top-level parents
        List<Code> parent = codeRepository.findByParentCodeId(null);
        for (Code code : parent) {
            if(this.get(CacheGroup.CODE, code.getCode(), Code.class) == null)
            {
                // Query up to lowest level
                Code nodes = codeRepository.findAllLeafNodes(code.getId());
                this.put(CacheGroup.CODE, code.getCode(), nodes);
            }
        }
    }
}
```

#### 1.4.3 `CacheController` 
Cache Controller that provides APIs for managing and calling cache from external sources.

##### Key Features
1. Complete cache initialization
2. Complete initialization based on cache group name
3. Removes `cache key` of a specific cache group

##### Basic REST API Example
```java
@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheRestController {

    private final MyCacheService myCacheService;

    @GetMapping("/clear/{cacheGroupName}")
    public ApiResponse<Void> clear(@PathVariable String cacheGroupName) {
        if (StringUtils.isEmpty(cacheGroupName)) {
            myCacheService.clearAll();
        } else {
            myCacheService.clearByCacheGroup(CacheGroup.getByName(cacheGroupName));
        }
        return ApiResponse.ok(null);
    }

    @GetMapping("/evict/{cacheGroupName}/{cacheKey}")
    public ApiResponse<Void> evict(@PathVariable String cacheGroupName, @PathVariable String cacheKey) {

        CacheGroup cacheGroup = CacheGroup.getByName(cacheGroupName);
        if (cacheGroup == null) {
            return ApiResponse.fail(new CustomException(ErrorCode.NOT_FOUND_ELEMENT));
        }

        myCacheService.evict(cacheGroup, cacheKey);
        return  ApiResponse.ok(null);
    }
}
```

## 2. Cache Configuration (SpringConfig and application.yml)  
Cache technology can be flexibly selected by `environment` through the `application.yml` file.

### 2.1 `CacheConfig`
Injects `dynamic dependencies` according to the `cache type` specified in environment properties. 
Uses `@Conditional` annotation and Spring's `@Configuration` for this purpose.

- **CacheConfig.java content**: 
    ```java
    @Configuration
    public class CacheConfig {
    
        @Bean
        @ConditionalOnProperty(name = "my.cache.type", havingValue = "caffeine")
        public CacheService caffeineCacheService() {
            return new CaffeineCacheService();
        }
    
        @Bean
        @ConditionalOnProperty(name = "my.cache.type", havingValue = "redis")
        public CacheService redisCacheService(RedisConnectionFactory connectionFactory) {
            return new RedisCacheService(getRedisTemplate(connectionFactory));
        }
    }
    ```
`@ConditionalOnProperty`: Activates specific Bean based on YML setting value

### 2.2 `application-{profile}.yml`
Select `cache technology` through environment-specific yml files.
- **application-local.yml content**:
    ```yaml
    cache:
      type: caffeine # Cache type
      
    ```
- **application-dev.yml content**:
    ```yaml
    cache:
      type: redis # Cache type
      
    ```

## 3. Cache Usage Examples
The following usage example shows how to declare and use MyCacheService.  
Briefly explaining the process:   
`Cache lookup -> Return if exists, Query DB and add to cache if not exists, then return`

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class CodeServiceImpl implements CodeService {
    private final MyCacheService myCacheService;

    @Override
    public Code findByCode(CodeSearchCondition condition) {
        if (condition.getCode() != null) {
``` 