package skcc.arch.user.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCreateRequest {

    @NotNull(message = "{javax.validation.constraints.NotNull.message}")
    private final String email;
    @NotNull(message = "{javax.validation.constraints.NotNull.message}")
    private final String password;
    @NotNull(message = "{javax.validation.constraints.NotNull.message}")
    private final String username;
}
