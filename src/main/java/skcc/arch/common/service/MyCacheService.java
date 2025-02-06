package skcc.arch.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skcc.arch.app.cache.CacheService;
import skcc.arch.app.cache.CaffeineCacheService;
import skcc.arch.code.domain.Code;
import skcc.arch.code.service.port.CodeRepository;
import skcc.arch.common.constants.CacheName;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyCacheService {

    private final CacheService cacheService;
    private final CodeRepository codeRepository;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void initCache() {

        if(cacheService instanceof CaffeineCacheService) {
            // 초기 적재할 캐시
            initCodeCache();
            log.info("캐시 적재 완료");
        }
    }

    /*
        캐시명.캐시KEY,값
        캐시명은 상수값을 사용한다
     */
    public void put(String cacheNm, String key, Object value) {
        cacheService.put(cacheNm + "." + key, value);
    }

    public <T> T get(String cacheNm, String key, Class<T> clazz) {
        return cacheService.get(cacheNm + "." + key, clazz);
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
            // 최하위 까지 조회
            Code nodes = codeRepository.findAllLeafNodes(code.getId());
            this.put(CacheName.CODE, code.getCode(), nodes);
        }
    }


}
