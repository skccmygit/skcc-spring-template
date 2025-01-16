package skcc.arch.user.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

    private final Long id;
    private final String email;
    private final String password;
    private final String username;
    private final UserStatus status;

    @Builder
    public User(Long id, String email, String password, String username, UserStatus status) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.status = status;
    }

    public static User from (UserCreateRequest userCreateRequest) {
        validateRequiredFields(userCreateRequest.getUsername(), userCreateRequest.getEmail(), userCreateRequest.getPassword());
        return User.builder()
                .email(userCreateRequest.getEmail())
                .username(userCreateRequest.getUsername())
                .password(userCreateRequest.getPassword())
                .status(UserStatus.PENDING)
                .build();
    }

    private static void validateRequiredFields(String username, String email, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username은 필수 값입니다.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email은 필수 값입니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password는 필수 값입니다.");
        }
    }

}
