package skcc.arch.app.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Profile("local")
public class CaffeineCacheService implements CacheService {

    private final Cache<String, Object> caffeineCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES) // 10분 TTL
            .maximumSize(500) // 최대 500개의 엔트리
            .build();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCachedValue(String key, Class<T> type) {
        return (T) caffeineCache.getIfPresent(key); // 캐시에서 값을 가져오고 타입 변환
    }

    @Override
    public void putValueInCache(String key, Object value) {
        caffeineCache.put(key, value); // 객체를 그대로 저장
    }

    @Override
    public void evictCache(String key) {
        caffeineCache.invalidate(key); // 캐시에서 삭제
    }
}