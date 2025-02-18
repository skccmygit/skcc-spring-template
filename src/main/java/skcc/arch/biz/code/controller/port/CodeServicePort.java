package skcc.arch.biz.code.controller.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skcc.arch.biz.code.domain.Code;
import skcc.arch.biz.code.domain.CodeCreate;
import skcc.arch.biz.code.domain.CodeSearch;
import skcc.arch.biz.code.domain.CodeUpdate;

public interface CodeServicePort {
    Code save(CodeCreate codeCreate);
    Code findById(Long id);
    Code findByIdWithChild(Long id);
    Page<Code> findByCode(Pageable pageable, CodeSearch codeSearch);
    Page<Code> findByConditionWithChild(Pageable pageable, CodeSearch codeSearch);
    Code update(CodeUpdate codeUpdate);
    Code findByCode(CodeSearch codeSearch);
}
