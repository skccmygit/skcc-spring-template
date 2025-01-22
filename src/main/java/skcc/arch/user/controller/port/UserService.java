package skcc.arch.user.controller.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skcc.arch.user.domain.User;
import skcc.arch.user.domain.UserCreateRequest;

import java.util.List;

public interface UserService {

    // 회원가입 메서드
    User create(UserCreateRequest userCreateRequest);

    // 로그인 처리
    String authenticate(String email, String rawPassword);

    // 전체 사용자 조회
    List<User> findAllUsers();

    Page<User> findAll(Pageable pageable);

    // 아이디로 사용자 조회
    User getById(Long id);

    // ADMIN 사용자 조회
    Page<User> findAdminUsers(Pageable pageable);
}
