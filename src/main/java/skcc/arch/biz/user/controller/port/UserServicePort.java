package skcc.arch.biz.user.controller.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skcc.arch.biz.user.controller.request.UserCreateRequest;
import skcc.arch.biz.user.controller.request.UserUpdateRequest;
import skcc.arch.biz.user.domain.User;
import skcc.arch.biz.user.domain.UserCreate;

import java.util.List;

public interface UserServicePort {

    // 회원가입 메서드
    User signUp(UserCreate userCreate);

    // 로그인 처리
    String authenticate(String email, String rawPassword);

    // 전체 사용자 조회
    List<User> findAllUsers();

    Page<User> findAll(Pageable pageable);

    // 아이디로 사용자 조회
    User getById(Long id);

    // ADMIN 사용자 조회
    Page<User> findAdminUsers(Pageable pageable);

    // 사용자 상태 변경
    @Deprecated
    User updateUserStatus(User user);

    // 사용자 정보 변경
    User updateUser(User user);
}
