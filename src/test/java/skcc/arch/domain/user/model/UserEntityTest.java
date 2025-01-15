package skcc.arch.domain.user.model;

import org.junit.jupiter.api.Test;
import skcc.arch.domain.user.dto.request.UserCreateRequestDto;
import skcc.arch.domain.user.infrastructure.jpa.UserEntity;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void createUserEntity_정상적으로_생성된다() {
        //given
        String username = "홍길동";
        String email = "email@sk.com";
        String password = "password123";

        UserCreateRequestDto userCreateRequestDto = UserCreateRequestDto.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();

        //when
        UserEntity userEntity = new UserEntity(userCreateRequestDto);

        //then
        assertNotNull(userEntity);
        assertEquals(username, userEntity.getUsername());
        assertEquals(email, userEntity.getEmail());
        assertEquals(password, userEntity.getPassword());
        assertEquals(UserStatus.PENDING, userEntity.getStatus());
    }

    @Test
    void validateRequiredFields_필수값_없으면_예외발생() {
        // Case 1: Null username
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new UserEntity(UserCreateRequestDto.builder().username(null).email("email").password("password").build()));
        assertEquals("Username은 필수 값입니다.", exception.getMessage());

        // Case 2: Blank email
        exception = assertThrows(
                IllegalArgumentException.class,
                () -> new UserEntity(UserCreateRequestDto.builder().username("name").email(null).password("password").build()));
        assertEquals("Email은 필수 값입니다.", exception.getMessage());

        // Case 3: Null password
        exception = assertThrows(
                IllegalArgumentException.class,
                () -> new UserEntity(UserCreateRequestDto.builder().username("name").email("email").password(null).build()));
        assertEquals("Password는 필수 값입니다.", exception.getMessage());
    }

    @Test
    void toUserDto_정확한_DTO_생성() {
        //given
        String username = "홍길동";
        String email = "email@sk.com";
        String password = "password123";

        UserCreateRequestDto userCreateRequestDto = UserCreateRequestDto.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();

        //when
        UserEntity userEntity = new UserEntity(userCreateRequestDto);

        //when
        User user = userEntity.toUserDto();

        //then
        assertNotNull(user);
        assertEquals(userEntity.getId(), user.getId());
        assertEquals(userEntity.getEmail(), user.getEmail());
        assertEquals(userEntity.getPassword(), user.getPassword());
        assertEquals(userEntity.getUsername(), user.getUsername());
        assertEquals(userEntity.getStatus(), user.getStatus());
    }

}