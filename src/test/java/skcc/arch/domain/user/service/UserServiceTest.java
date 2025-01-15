package skcc.arch.domain.user.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import skcc.arch.app.exception.CustomException;
import skcc.arch.domain.user.model.User;
import skcc.arch.domain.user.model.UserStatus;
import skcc.arch.domain.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this); // Mockito 초기화
    }

    @Test
    void 존재하지_않는_이메일로_로그인_시도() {
        // given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        // when, then
        assertThrows(CustomException.class, () -> userService.login(email, "anyPassword"));
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void 잘못된_비밀번호_로그인_시도() {
        // given
        String email = "test@example.com";
        String rawPassword = "1234";
        String encodedPassword = "encoded1234";

        User mockUser = new User(1L,email, encodedPassword, "username", UserStatus.PENDING);
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword))
                .thenReturn(false);

        // when, then
        assertThrows(CustomException.class, () -> userService.login(email, rawPassword));
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

    @Test
    void 정상적인_비밀번호_로그인_시도() {
        // given
        String email = "test@example.com";
        String rawPassword = "1234";
        String encodedPassword = "encoded1234";

        User mockUser = new User(1L,email, encodedPassword, "username", UserStatus.PENDING);
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword))
                .thenReturn(true);

        // when
        User foundUser = userService.login(email, rawPassword);

        // then
        assertNotNull(foundUser);
        assertEquals(email, foundUser.getEmail());
        verify(userRepository, times(1)).findByEmail(email); // 호출 확인
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

}