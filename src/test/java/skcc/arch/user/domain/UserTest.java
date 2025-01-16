package skcc.arch.user.domain;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    @Test
    public void UserCreate_객체로_생성() throws Exception {
        //given
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .username("홍길동")
                .email("email@sk.com")
                .password("password123")
                .build();

        //when
        User user = User.from(userCreateRequest);

        //then
        assertThat(user.getId()).isNull();
        assertThat(user.getUsername()).isEqualTo(userCreateRequest.getUsername());
        assertThat(user.getEmail()).isEqualTo(userCreateRequest.getEmail());
        assertThat(user.getPassword()).isEqualTo(userCreateRequest.getPassword());
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
    }


    @Test
    void UserCreate_필수_값이_없을_경우_에러_발생() {
        // Case 1: Null username
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> User.from(UserCreateRequest.builder().username("").email("email").password("password").build()));

        assertThat(exception.getMessage()).isEqualTo("Username은 필수 값입니다.");

        // Case 2: Blank email
        exception = assertThrows(IllegalArgumentException.class,
                () -> User.from(UserCreateRequest.builder().username("홍길동").email("").password("password").build()));
        assertThat(exception.getMessage()).isEqualTo("Email은 필수 값입니다.");

        // Case 3: Null password
        exception = assertThrows(IllegalArgumentException.class,
                () -> User.from(UserCreateRequest.builder().username("홍길동").email("email").password("").build()));
        assertThat(exception.getMessage()).isEqualTo("Password는 필수 값입니다.");
    }
}