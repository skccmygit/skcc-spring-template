package skcc.arch.code.controller.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skcc.arch.code.domain.Code;
import skcc.arch.code.domain.CodeCreateRequest;
import skcc.arch.code.domain.CodeSearchCondition;
import skcc.arch.code.service.dto.CodeDto;

public interface CodeService {
    Code save(CodeCreateRequest codeCreateRequest);
    Code findById(Long id);
    CodeDto findByIdWithChild(Long id);
    Code findByCode(String code);
    Page<Code> findAll(Pageable pageable);
    CodeDto findByCodeWithChild(CodeSearchCondition condition);
}
