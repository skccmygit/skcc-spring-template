package skcc.arch.domain.user.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import skcc.arch.domain.user.infrastructure.jpa.UserEntity;
import skcc.arch.domain.user.infrastructure.jpa.UserJpaRepository;
import skcc.arch.domain.user.model.User;
import skcc.arch.domain.user.dto.request.UserCreateRequestDto;
import skcc.arch.domain.user.repository.UserRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(UserEntity::toUserDto);
    }

    @Override
    public User save(UserCreateRequestDto userCreateRequestDto) {
        return userJpaRepository.save(new UserEntity(userCreateRequestDto)).toUserDto();
    }
}
