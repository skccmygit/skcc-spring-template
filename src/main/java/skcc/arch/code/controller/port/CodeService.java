package skcc.arch.code.controller.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skcc.arch.code.domain.Code;
import skcc.arch.code.domain.CodeCreateRequest;
import skcc.arch.code.domain.CodeSearchCondition;
import skcc.arch.code.service.dto.CodeDto;

public interface CodeService {
    Code save(CodeCreateRequest codeCreateRequest);
    CodeDto findById(Long id);
    CodeDto findByIdWithChild(Long id);
    Page<CodeDto> findByCode(Pageable pageable, CodeSearchCondition condition);
    Page<CodeDto> findByCodeWithChild(Pageable pageable, CodeSearchCondition condition);
}
