package skcc.arch.user.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class UserCreateRequest {

    private final String email;
    @Setter
    private String password;
    private final String username;

    @Builder
    public UserCreateRequest(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

}
