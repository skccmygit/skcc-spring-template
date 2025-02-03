package skcc.arch.code.controller.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skcc.arch.code.domain.Code;
import skcc.arch.code.domain.CodeCreateRequest;
import skcc.arch.code.domain.CodeSearchCondition;
import skcc.arch.code.domain.CodeUpdateRequest;

public interface CodeService {
    Code save(CodeCreateRequest codeCreateRequest);
    Code findById(Long id);
    Code findByIdWithChild(Long id);
    Page<Code> findByCode(Pageable pageable, CodeSearchCondition condition);
    Page<Code> findByCodeWithChild(Pageable pageable, CodeSearchCondition condition);
    Code update(CodeUpdateRequest codeUpdateRequest);
}
