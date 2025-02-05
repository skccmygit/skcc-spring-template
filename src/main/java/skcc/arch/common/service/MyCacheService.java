package skcc.arch.common.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import skcc.arch.app.cache.CacheService;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyCacheService {

    private final CacheService cacheService;

    // 초기 데이터 - 보통 DB나 외부 API에서 가져오는 데이터
    private final Map<String, String> initialData = Map.of(
            "key1", "value1",
            "key2", "value2",
            "key3", "value3"
    );

    @PostConstruct
    public void initCache() {
        log.info("initCache Start");
        cacheService.putValueInCache("initialData", initialData);
        log.info("initCache End");
    }
}
