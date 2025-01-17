package skcc.arch.user.service.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skcc.arch.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> getById(Long id);
    Optional<User> findByEmail(String email);
    User save(User user);
    List<User> findAllUsers();
    Page<User> findAll(Pageable pageable);
}
