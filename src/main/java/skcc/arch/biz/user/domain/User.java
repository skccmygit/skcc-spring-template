package skcc.arch.biz.user.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Getter
public class User {

    private final Long id;
    private final String email;
    private final String password;
    private final String username;
    private final UserRole role;
    private final UserStatus status;
    private final LocalDateTime createdDate;
    private final LocalDateTime lastModifiedDate;

    @Builder
    public User(Long id, String email, String password, String username, UserRole role, UserStatus status, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
        this.status = status;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * 사용자를 생성할때만 사용
     *
     * @param userCreate 사용자 생성 모델
     * @param passwordEncoder 비밀번호 생성 구현체
     * @return 사용자 모델
     */
    public static User from (UserCreate userCreate, PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(userCreate.getEmail())
                .username(userCreate.getUsername())
                .password(passwordEncoder.encode(userCreate.getPassword()))
                .role(UserRole.USER)
                .status(UserStatus.PENDING)
                // JPA의 경우 BaseEntity에 처리
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }

    /**
     * 상태 값을 변경할때 사용
     * @param requestStatus
     * @return
     */
    public User updateStatus(UserStatus requestStatus) {

        if (status == requestStatus) {
            throw new IllegalStateException("status is same");
        }

        return User.builder()
                .id(id)
                .email(email)
                .username(username)
                .password(password)
                .role(role)
                .status(requestStatus)
                .build();
    }
}
