package skcc.arch.biz.small.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import skcc.arch.app.exception.CustomException;
import skcc.arch.app.exception.ErrorCode;
import skcc.arch.app.util.JwtUtil;
import skcc.arch.biz.user.service.UserService;
import skcc.arch.biz.mock.FakePasswordEncoder;
import skcc.arch.biz.mock.FakeUserRepositoryPort;
import skcc.arch.biz.user.controller.request.UserCreateRequest;
import skcc.arch.biz.user.domain.User;
import skcc.arch.biz.user.domain.UserStatus;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    private UserService userService;
    private final int TOTAL_USER_COUNT = 45;
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        FakeUserRepositoryPort fakeUserRepository = new FakeUserRepositoryPort();
        FakePasswordEncoder fakePasswordEncoder = new FakePasswordEncoder();
        jwtUtil = new JwtUtil("skcc-secret-key-skcc-secret-key-skcc-secret-key", 1800000);

        this.userService = new UserService(fakeUserRepository, fakePasswordEncoder, jwtUtil);

        for (int i = 1; i <= TOTAL_USER_COUNT; i++) {
            fakeUserRepository.save(User.builder()
                    .id((long) (i))
                    .email("test"+i+"@sk.com")
                    .password(fakePasswordEncoder.encode("password"))
                    .username("홍길동"+i)
                    .status(UserStatus.ACTIVE)
                    .build());
        }
    }

    @Test
    void userCreate_를_이용해_생성한다() throws Exception {
        //given
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username("홍길동")
                .email("email@sk.com")
                .password("password")
                .build();

        // when
        User result = userService.signUp(userCreateRequest.toModel());

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(result.getPassword()).isEqualTo("ENC_password");
    }

    @Test
    void 이미존재하는_사용자_생성시_에러발생() throws Exception {
        //given
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username("홍길동")
                .email("test1@sk.com")
                .password("password")
                .build();

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> userService.signUp(userCreateRequest.toModel()));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EXIST_ELEMENT);
    }

    @Test
    void 정상적인_계정으로_토큰생성_검증() throws Exception {
        //given
        String email = "test1@sk.com";
        String rawPassword = "password";

        //when
        String accessToken = userService.authenticate(email, rawPassword);

        //then
        assertThat(accessToken).isNotNull();
        assertThat(jwtUtil.extractUid(accessToken)).isEqualTo(email);
        assertTrue(jwtUtil.validateToken(accessToken));
    }

    @Test
    void 잘못된_정보로_로그인_시도() throws Exception {
        //given
        String email = "test1@sk.com";
        String rawPassword = "passwordXXX";

        //when
        CustomException exception = assertThrows(CustomException.class, () -> userService.authenticate(email, rawPassword));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_MATCHED_PASSWORD);

    }

    @Test
    void 존재하지_않은_이메일로_로그인_시도() throws Exception {
        //given
        String email = "empty@sk.com";
        String rawPassword = "password";

        //when
        CustomException exception =
                assertThrows(CustomException.class, () -> userService.authenticate(email, rawPassword));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_ELEMENT);
    }

    @Test
    void 전체_사용자_조회() throws Exception {
        //given

        //when
        List<User> allUsers = userService.findAllUsers();

        //then
        assertThat(allUsers.size()).isEqualTo(TOTAL_USER_COUNT);

    }

    @Test
    void 페이지정보를_이용한_사용자_조회() throws Exception {
        //given
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(0, pageSize);

        //when
        Page<User> users = userService.findAll(pageRequest);

        //then
        assertThat(users.getTotalElements()).isEqualTo(TOTAL_USER_COUNT);
        assertThat(users.getContent().size()).isEqualTo(pageSize);
        assertThat(users.getTotalPages()).isEqualTo(TOTAL_USER_COUNT % pageSize == 0 ? TOTAL_USER_COUNT / pageSize : TOTAL_USER_COUNT / pageSize + 1);

    }

    @Test
    void ID로_사용자_조회() throws Exception {
        //given
        Long id = 1L;

        //when
        User user = userService.getById(id);

        //then
        assertThat(user.getId()).isEqualTo(id);

    }

}