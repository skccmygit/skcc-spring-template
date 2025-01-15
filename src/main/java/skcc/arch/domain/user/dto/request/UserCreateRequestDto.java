package skcc.arch.domain.user.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
}
