package skcc.arch.biz.user.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
public class UserCreate {

    private final String email;
    private final String password;
    private final String username;

}
