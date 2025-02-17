package skcc.arch.biz.user.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepositoryJpa extends JpaRepository<UserEntity, Long>  {

    Optional<UserEntity> findByEmail(String email);
}