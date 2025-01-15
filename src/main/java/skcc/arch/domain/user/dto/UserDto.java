package skcc.arch.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import skcc.arch.domain.user.model.UserEntity;
import skcc.arch.domain.user.model.UserStatus;

@Getter
public class UserDto {

    private final Long id;
    private final String email;
    private final String password;
    private final String username;
    private UserStatus status;

    @Builder
    public UserDto(Long id, String email, String password, String username, UserStatus status) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.status = status;
    }

    public static UserDto fromEntity(UserEntity entity) {
        return UserDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .status(entity.getStatus())
                .build();
    }
}
