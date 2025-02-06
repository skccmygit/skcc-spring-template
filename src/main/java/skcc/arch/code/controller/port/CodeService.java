package skcc.arch.code.controller.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skcc.arch.code.domain.Code;
import skcc.arch.code.domain.CodeCreateRequest;
import skcc.arch.code.domain.CodeSearchCondition;
import skcc.arch.code.domain.CodeUpdateRequest;

import java.util.List;

public interface CodeService {
    Code save(CodeCreateRequest codeCreateRequest);
    Code findById(Long id);
    Code findByIdWithChild(Long id);
    Page<Code> findByCode(Pageable pageable, CodeSearchCondition condition);
    Page<Code> findByConditionWithChild(Pageable pageable, CodeSearchCondition condition);
    Code update(CodeUpdateRequest codeUpdateRequest);
    Code findByCode(CodeSearchCondition condition);
    List<Code> findByParentIsNull();
    Code findAllLeafNodes(Long id);
}
