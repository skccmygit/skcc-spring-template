package skcc.arch.code.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodeRepositoryJpa extends JpaRepository<CodeEntity, Long> {
    Optional<CodeEntity> findTopByParentCodeIdOrderBySeqDesc(Long parentCodeId);
    boolean existsCodeEntityByParentCodeIdAndSeqOrderBySeqDesc(Long parentCodeId, Integer seq);
}
