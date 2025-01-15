package skcc.arch.domain.user.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import skcc.arch.domain.user.dto.UserDto;
import skcc.arch.domain.user.dto.request.UserCreateRequestDto;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void createUserEntity_정상적으로_생성된다() {
        //given
        String username = "홍길동";
        String email = "email@sk.com";
        String password = "password123";

        //when
        UserEntity userEntity = new UserEntity(username, email, password);

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
                () -> new UserEntity(null, "email@sk.com", "password123"));
        assertEquals("Username은 필수 값입니다.", exception.getMessage());

        // Case 2: Blank email
        exception = assertThrows(
                IllegalArgumentException.class,
                () -> new UserEntity("홍길동", " ", "password123"));
        assertEquals("Email은 필수 값입니다.", exception.getMessage());

        // Case 3: Null password
        exception = assertThrows(
                IllegalArgumentException.class,
                () -> new UserEntity("홍길동", "email@sk.com", null));
        assertEquals("Password는 필수 값입니다.", exception.getMessage());
    }

    @Test
    void toUserDto_정확한_DTO_생성() {
        //given
        String username = "홍길동";
        String email = "email@sk.com";
        String password = "password123";

        UserEntity userEntity = new UserEntity(username, email, password);

        //when
        UserDto userDto = userEntity.toUserDto();

        //then
        assertNotNull(userDto);
        assertEquals(userEntity.getId(), userDto.getId());
        assertEquals(userEntity.getEmail(), userDto.getEmail());
        assertEquals(userEntity.getPassword(), userDto.getPassword());
        assertEquals(userEntity.getUsername(), userDto.getUsername());
        assertEquals(userEntity.getStatus(), userDto.getStatus());
    }

    @Test
    void validateRequiredFields_필수값을_체크한다() throws Exception {
        //given
        UserCreateRequestDto userCreateRequestDto = UserCreateRequestDto.builder()
                .email("email@sk.com")
                .username("홍길동")
                .build();

        //when, then
        Assertions.assertThrows(IllegalArgumentException.class, userCreateRequestDto::toEntity);

    }
}