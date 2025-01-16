package skcc.arch.medium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;
import skcc.arch.app.exception.CustomException;
import skcc.arch.app.exception.ErrorCode;
import skcc.arch.user.controller.port.UserService;
import skcc.arch.user.domain.User;
import skcc.arch.user.domain.UserCreateRequest;
import skcc.arch.user.domain.UserStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@SqlGroup({
        @Sql(value = "/sql/user-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void userCreate_를_이용해_생성한다() throws Exception {
        //given
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username("홍길동")
                .email("hongildong@sk.com")
                .password("password")
                .build();

        // when
        User result = userService.create(userCreateRequest);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
    }

    @Test
    void 정상적인_비밀번호_로그인_시도() throws Exception {
        //given
        String email = "test1@sk.com";
        String rawPassword = "password";

        BDDMockito.given(passwordEncoder.matches(rawPassword, "password"))
                .willReturn(true);


        //when
        User user = userService.login(email, rawPassword);


        //then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);

    }

    @Test
    void 잘못된_비밀번호_로그인_시도() throws Exception {
        //given
        String email = "test1@sk.com";
        String rawPassword = "XXXXXX";


        //when & then
        CustomException exception = assertThrows(CustomException.class, () -> userService.login(email, rawPassword));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_MATCHED_PASSWORD);

    }
    
    
}
