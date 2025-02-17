package skcc.arch.biz.common.controller;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import skcc.arch.app.dto.ApiResponse;
import skcc.arch.app.exception.CustomException;
import skcc.arch.app.exception.ErrorCode;
import skcc.arch.biz.common.constants.CacheGroup;
import skcc.arch.biz.common.service.MyCacheService;

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
