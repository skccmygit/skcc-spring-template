package skcc.arch.biz.code.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CodeRepositoryJpa extends JpaRepository<CodeEntity, Long> {
    Optional<CodeEntity> findTopByParentCodeIdOrderBySeqDesc(Long parentCodeId);
    boolean existsCodeEntityByParentCodeIdAndSeqOrderBySeqDesc(Long parentCodeId, Integer seq);
    List<CodeEntity> findByParentCodeId(Long parentCodeId);
    CodeEntity findByCode(String code);
}
