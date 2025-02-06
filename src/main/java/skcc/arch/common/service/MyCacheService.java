package skcc.arch.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skcc.arch.app.cache.CacheService;
import skcc.arch.app.cache.CaffeineCacheService;
import skcc.arch.app.cache.RedisCacheService;
import skcc.arch.code.domain.Code;
import skcc.arch.code.service.port.CodeRepository;
import skcc.arch.common.constants.CacheName;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyCacheService {

    public static final String DELIMITER = ":";
    private final CacheService cacheService;
    private final CodeRepository codeRepository;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void initCache() {

        // 메모리(로컬)
        if(cacheService instanceof CaffeineCacheService) {
            // 초기 적재할 캐시
            initCodeCache();
            log.info("캐시 적재 완료");
        }
        // 레디스(서버)
        else if (cacheService instanceof RedisCacheService) {
            // 초기로딩이 필요한지?
            initCodeCache();
        }
    }

    /*
        캐시명.캐시 KEY,값
        캐시명은 상수값을 사용한다
     */
    public void put(String cacheNm, String key, Object value) {
        try {
            cacheService.put(cacheNm + DELIMITER + key, value);
        } catch (Exception e) {
            log.error(" cache put error : {}", e.getMessage());
        }
    }

    public <T> T get(String cacheNm, String key, Class<T> clazz) {
        T t;
        try {
            t = cacheService.get(cacheNm + DELIMITER + key, clazz);
        } catch (Exception e) {
            log.error(" cache get error : {}", e.getMessage());
            t = null;
        }
        return t;
    }

    public void evict(String cacheNm, String key) {
        try {
            cacheService.evict(cacheNm + DELIMITER + key);
        } catch (Exception e) {
            log.error(" cache evict error : {}", e.getMessage());
        }
    }

    public void clearAll() {
        try {
            cacheService.clearAll();
        } catch (Exception e) {
            log.error(" cache clearAll error : {}", e.getMessage());
        }
    }

    public void clearCacheName(String cacheName) {

        try {
            cacheService.clearByCacheName(cacheName);
        } catch (Exception e) {
            log.error(" cache clearCacheName error : {}", e.getMessage());
        }
    }


    /**
     * 비즈니스 요건에 맞게 캐시 설계
     *
     * Sample - 코드 도메인의 최상위 부모만 캐시에 적재한다.
     *       Cache Name : code
     *       KEY: 부모의 코드값
     *       VALUE: Code 모델 (최하위 요소까지 포함)
     */
    private void initCodeCache() {
        // 최상위 부모 조회
        List<Code> parent = codeRepository.findByParentCodeId(null);
        for (Code code : parent) {
            if(this.get(CacheName.CODE, code.getCode(), Code.class) == null)
            {
                // 최하위 까지 조회
                Code nodes = codeRepository.findAllLeafNodes(code.getId());
                this.put(CacheName.CODE, code.getCode(), nodes);
            }
        }
    }

}
