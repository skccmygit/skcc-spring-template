package skcc.arch.code.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import skcc.arch.app.dto.ApiResponse;
import skcc.arch.app.dto.PageInfo;
import skcc.arch.code.controller.port.CodeService;
import skcc.arch.code.controller.response.CodeResponse;
import skcc.arch.code.domain.Code;
import skcc.arch.code.domain.CodeCreateRequest;
import skcc.arch.code.domain.CodeSearchCondition;
import skcc.arch.code.domain.CodeUpdateRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/codes")
public class CodeRestController {
    private final CodeService codeService;
    
    @PostMapping
    public ApiResponse<CodeResponse> createCode(@RequestBody CodeCreateRequest codeCreateRequest) {
        return ApiResponse.ok(CodeResponse.from(codeService.save(codeCreateRequest)));
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ApiResponse<CodeResponse> getCode(@PathVariable Long id
            , @RequestParam(required = false, defaultValue = "false") boolean withChild) {

        if (withChild) {
            return ApiResponse.ok(CodeResponse.from(codeService.findByIdWithChild(id)));
        } else {
            return ApiResponse.ok(CodeResponse.from(codeService.findById(id)));
        }

    }

    // 다건 조회
    @GetMapping
    public ApiResponse<List<CodeResponse>> getCodeList(Pageable pageable, CodeSearchCondition condition
            , @RequestParam(required = false, defaultValue = "false") boolean withChild) {

        Page<Code> result;
        if(withChild) {
            result = codeService.findByConditionWithChild(pageable, condition);
        }else {
            result = codeService.findByCondition(pageable, condition);
        }
        return ApiResponse.ok(
            result.getContent()
                  .stream()
                  .map(CodeResponse::from)
                  .toList(),
            PageInfo.fromPage(result)
        );

    }

    @PatchMapping
    public ApiResponse<CodeResponse> updateCode(@RequestBody @Valid CodeUpdateRequest codeUpdateRequest) {
        return ApiResponse.ok(CodeResponse.from(codeService.update(codeUpdateRequest)));
    }
}
