package skcc.arch.file.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FIleRepositoryJpa extends JpaRepository<FileEntity, Long> {
}
