package skcc.arch.code.service.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skcc.arch.code.domain.Code;
import skcc.arch.code.domain.CodeSearchCondition;

import java.util.Optional;

public interface CodeRepository {
    Code save(Code code);
    Optional<Code> findById(Long id);
    Optional<Code> findByIdWithChild(Long id);
    Page<Code> findByCondition(Pageable pageable, CodeSearchCondition condition);
    Page<Code> findByConditionWithChild(Pageable pageable, CodeSearchCondition condition);
    Optional<Code> findTopByParentCodeIdOrderBySeqDesc(Long parentCodeId);
    boolean existsCodeEntityByParentCodeIdAndSeqOrderBySeqDesc(Long parentCodeId, Integer seq);
    Code update(Code code);
}
