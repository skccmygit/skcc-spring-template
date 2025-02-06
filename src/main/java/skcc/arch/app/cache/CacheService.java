package skcc.arch.app.cache;

public interface CacheService {
    <T> T get(String key, Class<T> type);
    void put(String key, Object value);
    void evict(String key);
}
