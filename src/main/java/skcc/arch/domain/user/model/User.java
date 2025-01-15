package skcc.arch.domain.user.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

    private final Long id;
    private final String email;
    private final String password;
    private final String username;
    private UserStatus status;

    @Builder
    public User(Long id, String email, String password, String username, UserStatus status) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.status = status;
    }




}
