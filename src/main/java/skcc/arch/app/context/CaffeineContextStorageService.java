package skcc.arch.app.context;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import skcc.arch.app.util.AuthUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CaffeineContextStorageService implements ContextStorageService {

    private final Cache<String, Map<String, Object>> cache;
    private final AuthUtil authUtil;

    public CaffeineContextStorageService() {
        this.cache = Caffeine.newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)  // 30분 동안 접근 없으면 자동 삭제
                .maximumSize(1000)                                // 최대 1000명의 사용자 저장소 관리
                .build();
        this.authUtil = new AuthUtil();  // JWT 유틸리티 클래스 주입
    }

    public CaffeineContextStorageService(Cache<String, Map<String, Object>> cache, AuthUtil authUtil) {

        this.cache = cache;
        this.authUtil = authUtil;
    }

    /**
     * SecurityContext 에서 uid를 추출
     */
    private String getUidFromSecurityContext() {
        // JWT에서 uid를 추출. 구현 방식은 JwtUtil에 따라 다름

        String uid = authUtil.getUID();
        if (uid == null || uid.isEmpty()) {
            throw new IllegalStateException("유효한 JWT 토큰에서 uid를 추출하지 못했습니다.");
        }
        return uid;
    }

    @Override
    public void set(String key, Object value) {
        String uid = getUidFromSecurityContext();
        Map<String, Object> userContext = cache.get(uid, k -> new ConcurrentHashMap<>());
        userContext.put(key, value);
        log.info("Set value for uid={}, key={}, value={}", uid, key, value);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        String uid = getUidFromSecurityContext();
        Map<String, Object> userContext = cache.getIfPresent(uid);

        if (userContext == null || !userContext.containsKey(key)) {
            log.warn("Key={} not found for uid={}", key, uid);
            return null;
        }

        Object value = userContext.get(key);
        if (type.isInstance(value)) {
            return type.cast(value);
        } else {
            log.warn("Value for key={} is not instance of {}", key, type.getName());
            return null;
        }
    }

    @Override
    public void remove(String key) {
        String uid = getUidFromSecurityContext();
        Map<String, Object> userContext = cache.getIfPresent(uid);

        if (userContext != null) {
            userContext.remove(key);
            log.info("Removed key={} for uid={}", key, uid);
        } else {
            log.warn("No context found for uid={} to remove key={}", uid, key);
        }
    }

    @Override
    public void clear() {
        String uid = getUidFromSecurityContext();
        cache.invalidate(uid);
        log.info("Cleared context for uid={}", uid);
    }
}