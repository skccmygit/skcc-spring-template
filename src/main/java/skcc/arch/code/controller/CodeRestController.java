package skcc.arch.code.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import skcc.arch.app.dto.ApiResponse;
import skcc.arch.app.dto.PageInfo;
import skcc.arch.code.controller.port.CodeService;
import skcc.arch.code.domain.Code;
import skcc.arch.code.domain.CodeCreateRequest;
import skcc.arch.code.domain.CodeSearchCondition;
import skcc.arch.code.service.dto.CodeDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/codes")
public class CodeRestController {
    private final CodeService codeService;
    
    @PostMapping
    public ApiResponse<Code> createCode(@RequestBody CodeCreateRequest codeCreateRequest) {
        //TODO-CODE RESPONSE DTO 생성 필요
        return ApiResponse.ok(codeService.save(codeCreateRequest));
    }


    // 단건 조회
    @GetMapping("/{id}")
    public ApiResponse<CodeDto> getCode(@PathVariable Long id
            , @RequestParam(required = false, defaultValue = "false") boolean withChild) {

        if(withChild) {
            return ApiResponse.ok(codeService.findByIdWithChild(id));
        }else
            return ApiResponse.ok(codeService.findById(id));

    }

    // 다건 조회
    @GetMapping
    public ApiResponse<List<CodeDto>> getCodeList(Pageable pageable, CodeSearchCondition condition
            , @RequestParam(required = false, defaultValue = "false") boolean withChild) {

        Page<CodeDto> result;
        if(withChild) {
            result = codeService.findByCodeWithChild(pageable, condition);
        }else {
            result = codeService.findByCode(pageable, condition);
        }
        return ApiResponse.ok(result.getContent(), PageInfo.fromPage(result));

    }
    
}
