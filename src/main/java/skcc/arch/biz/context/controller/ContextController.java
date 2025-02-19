package skcc.arch.biz.context.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import skcc.arch.app.context.ContextStorageService;
import skcc.arch.app.dto.ApiResponse;

import java.util.Map;

@RestController
@RequestMapping("/api/context")
@RequiredArgsConstructor
public class ContextController {

    private final ContextStorageService ctxService;

    @PostMapping(value = "/set/{key}")
    public String setContext(@PathVariable String key, @RequestBody Map<String, Object> data) {
        ctxService.set(key, data);
        return "ctx-set-ok";
    }


    @GetMapping(value = "/get/{key}")
    public ApiResponse<?> getContext(@PathVariable String key) {
        return ApiResponse.ok(ctxService.get(key, Map.class));
    }
}
