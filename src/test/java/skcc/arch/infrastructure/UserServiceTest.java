package skcc.arch.infrastructure;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import skcc.arch.app.exception.CustomException;
import skcc.arch.app.exception.ErrorCode;
import skcc.arch.user.controller.port.UserService;
import skcc.arch.user.domain.User;
import skcc.arch.user.domain.UserCreateRequest;
import skcc.arch.user.domain.UserStatus;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@SqlGroup({
        @Sql(value = "/sql/user-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void userCreate_를_이용해_생성한다() throws Exception {
        //given
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username("홍길동")
                .email("abcd@sk.com")
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


        //when
        User user = userService.authenticate(email, rawPassword);


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
        CustomException exception = assertThrows(CustomException.class, () -> userService.authenticate(email, rawPassword));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_MATCHED_PASSWORD);
    }

    @Test
    void ID로_사용자정보를_가져온다() throws Exception {
        //given
        Long id = 2L;

        //when
        User user = userService.getById(id);


        //then
        assertThat(user.getId()).isNotNull();
        assertThat(user.getId()).isEqualTo(id);
    }

    @Test
    void 전체_사용자를_조회한다() throws Exception {
        //given

        //when
        List<User> allUsers = userService.findAllUsers();

        //then
        assertThat(allUsers.size()).isGreaterThan(0);

    }

    @Test
    void 페이지정보를_이용하여_조회한다() throws Exception {
        //given
        int pageSize = 2;
        PageRequest pageRequest = PageRequest.of(1, pageSize);
        //when
        Page<User> users = userService.findAll(pageRequest);

        //then
        assertThat(users.getContent().size()).isEqualTo(pageSize);

    }

    
}
