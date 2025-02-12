package skcc.arch.code.controller.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skcc.arch.code.controller.request.CodeSearchRequest;
import skcc.arch.code.domain.Code;
import skcc.arch.code.controller.request.CodeCreateRequest;
import skcc.arch.code.controller.request.CodeUpdateRequest;

public interface CodeServicePort {
    Code save(CodeCreateRequest codeCreateRequest);
    Code findById(Long id);
    Code findByIdWithChild(Long id);
    Page<Code> findByCode(Pageable pageable, CodeSearchRequest codeSearchRequest);
    Page<Code> findByConditionWithChild(Pageable pageable, CodeSearchRequest codeSearchRequest);
    Code update(CodeUpdateRequest codeUpdateRequest);
    Code findByCode(CodeSearchRequest codeSearchRequest);
}
