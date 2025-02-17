package skcc.arch.biz.small.user.infrastructure.mybatis;

import org.junit.jupiter.api.Test;
import skcc.arch.biz.user.domain.User;
import skcc.arch.biz.user.domain.UserStatus;
import skcc.arch.biz.user.infrastructure.mybatis.UserDto;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void from() {

        //given
        User user = User.builder()
                .username("username")
                .password("password")
                .email("email")
                .status(UserStatus.PENDING)
                .build();


        //when
        UserDto result = UserDto.from(user);

        //then
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getStatus(), result.getStatus());
    }

    @Test
    void toModel() {
        User user = User.builder()
                .username("username")
                .password("password")
                .email("email")
                .status(UserStatus.PENDING)
                .build();
        UserDto result = UserDto.from(user);

        //when
        User model = result.toModel();

        //then
        assertEquals(user.getUsername(), model.getUsername());
        assertEquals(user.getPassword(), model.getPassword());
        assertEquals(user.getEmail(), model.getEmail());
        assertEquals(user.getStatus(), model.getStatus());
    }

}