package skcc.arch.user.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import skcc.arch.user.domain.User;
import skcc.arch.user.infrastructure.mybatis.UserDto;
import skcc.arch.user.infrastructure.mybatis.UserMybatisRepository;
import skcc.arch.user.service.port.UserRepository;

import java.util.List;
import java.util.Optional;

//@Repository
@RequiredArgsConstructor
public class UserMybatisRepositoryImpl implements UserRepository {

    private final UserMybatisRepository userMybatisRepository;

    @Override
    public Optional<User> findById(Long id) {
        return userMybatisRepository.findById(id).map(UserDto::toModel);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userMybatisRepository.findByEmail(email).map(UserDto::toModel);
    }

    @Override
    public User save(User user) {
        UserDto userDto = UserDto.from(user);
        Long savedCount = userMybatisRepository.save(userDto);
        if(savedCount == 0) {
            return null;
        }
        return userMybatisRepository.findById(userDto.getId()).map(UserDto::toModel)
                .orElse(null);

    }

    @Override
    public List<User> findAll() {
        return userMybatisRepository.findAll()
                .stream()
                .map(UserDto::toModel)
                .toList();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {

        // MyBatis 쿼리를 호출
        List<UserDto> userDtos = userMybatisRepository.findAllWithPageable(
                pageable.getOffset(),
                pageable.getPageSize()
        );

        // 총 데이터 개수를 가져오는 로직
        long totalCount = userMybatisRepository.countAll();

        // Page<User>로 변환
        List<User> users = userDtos.stream()
                .map(UserDto::toModel)
                .toList();

        // 반환: Page 구현체 생성
        return new PageImpl<>(users, pageable, totalCount);
    }
}
