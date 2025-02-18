package skcc.arch.biz.code.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import skcc.arch.biz.code.domain.CodeCreate;

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

    public CodeCreate toModel() {
        return CodeCreate.builder()
                .code(code)
                .codeName(codeName)
                .parentCodeId(parentCodeId)
                .description(description)
                .seq(seq)
                .build();
    }
}
