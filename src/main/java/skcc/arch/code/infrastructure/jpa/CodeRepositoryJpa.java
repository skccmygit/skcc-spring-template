package skcc.arch.code.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeRepositoryJpa extends JpaRepository<CodeEntity, Long> {
}
