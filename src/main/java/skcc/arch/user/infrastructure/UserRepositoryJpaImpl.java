package skcc.arch.user.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import skcc.arch.user.domain.User;
import skcc.arch.user.infrastructure.jpa.UserEntity;
import skcc.arch.user.infrastructure.jpa.UserRepositoryJpa;
import skcc.arch.user.service.port.UserRepository;

import java.util.List;
import java.util.Optional;

@Repository( value = "userRepositoryJpa")
@RequiredArgsConstructor
public class UserRepositoryJpaImpl implements UserRepository {

    private final UserRepositoryJpa userRepositoryJpa;

    @Override
    public Optional<User> findById(Long id) {
        return userRepositoryJpa.findById(id).map(UserEntity::toModel);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepositoryJpa.findByEmail(email)
                .map(UserEntity::toModel);
    }

    @Override
    public User save(User user) {
        return userRepositoryJpa.save(UserEntity.from(user)).toModel();
    }

    @Override
    public List<User> findAll() {
        return userRepositoryJpa.findAll()
                .stream()
                .map(UserEntity::toModel)
                .toList();

    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepositoryJpa.findAll(pageable)
                .map(UserEntity::toModel);
    }
}
