package skcc.arch.common.controller;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import skcc.arch.app.dto.ApiResponse;
import skcc.arch.common.service.MyCacheService;

@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheRestController {

    private final MyCacheService myCacheService;

    @GetMapping("/clear/{cacheName}")
    public ApiResponse<Void> clear(@PathVariable String cacheName) {
        if (StringUtils.isEmpty(cacheName)) {
            myCacheService.clearAll();
        } else {
            myCacheService.clearCacheName(cacheName);
        }
        return ApiResponse.ok(null);
    }

    @GetMapping("/evict/{cacheName}/{cacheKey}")
    public ApiResponse<Void> evict(@PathVariable String cacheName, @PathVariable String cacheKey) {
        myCacheService.evict(cacheName, cacheKey);
        return  ApiResponse.ok(null);
    }
}
