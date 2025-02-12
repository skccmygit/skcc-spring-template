package skcc.arch.code.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CodeCreateRequest {

    @NotNull(message = "{javax.validation.constraints.NotNull.message}")
    private final String code;
    @NotNull(message = "{javax.validation.constraints.NotNull.message}")
    private final String codeName;
    private final Long parentCodeId;
    private final int seq;
    private final String description;

}
