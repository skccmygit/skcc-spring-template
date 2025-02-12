package skcc.arch.user.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import skcc.arch.user.domain.UserStatus;

@Getter
@Builder
public class UserUpdateRequest {

    @NotNull(message = "{javax.validation.constraints.NotNull.message}")
    private final String email;
    private final String username;
    private final String password;
    private final UserStatus status;
}
