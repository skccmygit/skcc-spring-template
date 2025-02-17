package skcc.arch.biz.user.service.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skcc.arch.biz.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User save(User user);
    List<User> findAll();
    Page<User> findAll(Pageable pageable);
    Page<User> findAdminUsers(Pageable pageable);
    User updateStatus(User user);
}
