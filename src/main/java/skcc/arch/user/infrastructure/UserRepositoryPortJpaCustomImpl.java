package skcc.arch.user.infrastructure;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import skcc.arch.user.domain.User;
import skcc.arch.user.domain.UserRole;
import skcc.arch.user.infrastructure.jpa.QUserEntity;
import skcc.arch.user.infrastructure.jpa.UserEntity;
import skcc.arch.user.infrastructure.jpa.UserRepositoryJpa;
import skcc.arch.user.service.port.UserRepositoryPort;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryPortJpaCustomImpl implements UserRepositoryPort {

    private final UserRepositoryJpa userRepositoryJpa;
    private final JPAQueryFactory queryFactory;

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

    @Override
    public Page<User> findAdminUsers(Pageable pageable) {
        QUserEntity user = QUserEntity.userEntity;

        // 1. 기본 QueryDSL 쿼리 작성
        JPAQuery<UserEntity> query = queryFactory
                .selectFrom(user)
                .where(user.role.eq(UserRole.ADMIN)); // role이 ADMIN인 조건

        // 2. 페이징 처리
        long total = query.stream().count(); // 전체 데이터 개수 가져오기
        List<User> users = query
                .offset(pageable.getOffset()) // 시작 위치
                .limit(pageable.getPageSize()) // 페이지당 데이터 개수
                .fetch()
                .stream()
                .map(UserEntity::toModel)
                .toList();

        log.info("[Repository] users.size : {}", users.size());
        // 3. Page로 변환하여 반환
        return new PageImpl<>(users, pageable, total);

    }

    @Override
    public User updateStatus(User user) {
        return userRepositoryJpa.save(UserEntity.from(user)).toModel();
    }
}
