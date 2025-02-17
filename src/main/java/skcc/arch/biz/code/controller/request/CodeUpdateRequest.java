package skcc.arch.biz.code.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import skcc.arch.biz.code.domain.CodeUpdate;


/**
 * 코드 업데이트 요청 객체
 */

@Getter
@Builder
public class CodeUpdateRequest {

    private final Long id;
    @NotNull(message = "{javax.validation.constraints.NotNull.message}")
    private final String code;
    @NotNull(message = "{javax.validation.constraints.NotNull.message}")
    private final String codeName;
    @NotNull(message = "{javax.validation.constraints.NotNull.message}")
    private final int seq;
    private final Long parentCodeId;
    private final String description;
    private final boolean delYn;

    public CodeUpdate toModel() {
        return CodeUpdate.builder()
                        .id(id)
                        .code(code)
                        .codeName(codeName)
                        .parentCodeId(parentCodeId)
                        .seq(seq)
                        .delYn(delYn)
                        .description(description)
                        .build();
    }
}
