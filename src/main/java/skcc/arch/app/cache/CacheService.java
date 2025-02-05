package skcc.arch.app.cache;

public interface CacheService {
    <T> T getCachedValue(String key, Class<T> type);
    void putValueInCache(String key, Object value);
    void evictCache(String key);
}
