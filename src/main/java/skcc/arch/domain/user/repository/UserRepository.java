package skcc.arch.domain.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skcc.arch.domain.user.model.User;
import skcc.arch.domain.user.dto.request.UserCreateRequestDto;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    User save(UserCreateRequestDto user);
    List<User> findAllUsers();
    Page<User> findAll(Pageable pageable);
}
