package skcc.arch.user.infrastructure.jpa;

import org.junit.jupiter.api.Test;
import skcc.arch.user.domain.User;
import skcc.arch.user.domain.UserStatus;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

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
        UserEntity userEntity = UserEntity.from(user);

        //then
        assertEquals(user.getUsername(), userEntity.getUsername());
        assertEquals(user.getPassword(), userEntity.getPassword());
        assertEquals(user.getEmail(), userEntity.getEmail());
        assertEquals(user.getStatus(), userEntity.getStatus());
    }

    @Test
    void toModel() {
        //given
        User user = User.builder()
                .username("username")
                .password("password")
                .email("email")
                .status(UserStatus.PENDING)
                .build();
        UserEntity userEntity = UserEntity.from(user);

        //when
        User result = userEntity.toModel();

        //then
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getStatus(), result.getStatus());
    }
    
}