package skcc.arch.biz.code.service.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skcc.arch.biz.code.domain.Code;
import skcc.arch.biz.code.domain.CodeSearch;

import java.util.List;
import java.util.Optional;

public interface CodeRepositoryPort {
    Code save(Code code);
    Optional<Code> findById(Long id);
    Optional<Code> findByIdWithChild(Long id);
    Page<Code> findByCondition(Pageable pageable, CodeSearch search);
    Page<Code> findByConditionWithChild(Pageable pageable, CodeSearch search);
    Optional<Code> findTopByParentCodeIdOrderBySeqDesc(Long parentCodeId);
    boolean existsCodeEntityByParentCodeIdAndSeqOrderBySeqDesc(Long parentCodeId, Integer seq);
    Code update(Code code);
    List<Code> findByParentCodeId(Long parentCodeId);
    Code findByCode(CodeSearch search);
    Code findAllLeafNodes(Long id);
}
