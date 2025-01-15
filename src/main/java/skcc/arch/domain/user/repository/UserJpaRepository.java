package skcc.arch.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skcc.arch.domain.user.model.UserEntity;

import java.util.Optional;

public interface UserJpaRepository  extends JpaRepository<UserEntity, Long>  {

    Optional<UserEntity> findByEmail(String email);
}