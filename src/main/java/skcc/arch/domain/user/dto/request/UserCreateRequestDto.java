package skcc.arch.domain.user.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import skcc.arch.domain.user.model.UserEntity;

@Getter
@Setter
public class UserCreateRequestDto {

    private final String email;
    private String password;
    private final String username;

    @Builder
    public UserCreateRequestDto(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public UserEntity toEntity() {
        return new UserEntity(username, email, password);
    }
}
