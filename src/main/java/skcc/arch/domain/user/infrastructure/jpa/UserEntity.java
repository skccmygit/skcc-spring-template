package skcc.arch.domain.user.infrastructure.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import skcc.arch.domain.common.infrastructure.BaseEntity;
import skcc.arch.domain.user.dto.request.UserCreateRequestDto;
import skcc.arch.domain.user.model.User;
import skcc.arch.domain.user.model.UserStatus;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@ToString
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String username;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    // 도메인 설계에서 필요한 값들만 생성자를 통해 초기화
    public UserEntity(UserCreateRequestDto userCreateRequestDto) {
        validateRequiredFields(userCreateRequestDto.getUsername(), userCreateRequestDto.getEmail(), userCreateRequestDto.getPassword()); // 유효성 검사
        this.username = userCreateRequestDto.getUsername();
        this.email = userCreateRequestDto.getEmail();
        this.password = userCreateRequestDto.getPassword();
        this.status = UserStatus.PENDING;
    }

    private void validateRequiredFields(String username, String email, String password) {
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

    public static UserEntity fromUserDto(User user) {

        UserEntity userEntity = new UserEntity();
        userEntity.id = user.getId();
        userEntity.email = user.getEmail();
        userEntity.password = user.getPassword();
        userEntity.username = user.getUsername();
        userEntity.status = user.getStatus();
        return userEntity;
    }

    public User toUserDto() {
        return  new User(id, email, password, username, status);
    }

}