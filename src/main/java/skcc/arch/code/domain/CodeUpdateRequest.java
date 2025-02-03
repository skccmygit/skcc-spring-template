package skcc.arch.code.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;


/**
 * 코드 업데이트 요청 객체
 */

@Builder
@Getter
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
}
