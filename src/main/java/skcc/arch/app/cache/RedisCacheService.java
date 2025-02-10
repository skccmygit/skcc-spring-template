package skcc.arch.app.cache;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisCacheService implements CacheService {

    public static final String CACHE_DELIMITER = ":";
    public static final String PATTERN_ALL = "*";
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return type.cast(value); // 저장된 값을 필요한 타입으로 변환
    }

    @Override
    public void put(String key, Object value) {
        redisTemplate.opsForValue().set(key, value, 10, TimeUnit.MINUTES); // TTL 10분
    }

    @Override
    public void evict(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void clearAll() {
        clearKeysByPattern(PATTERN_ALL);
    }

    @Override
    public void clearByCacheGroup(String cacheName) {
        clearKeysByPattern(cacheName + CACHE_DELIMITER + PATTERN_ALL);
    }

    private void clearKeysByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern); // 특정 패턴의 키 검색
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys); // 해당 키 삭제
        }
    }
}