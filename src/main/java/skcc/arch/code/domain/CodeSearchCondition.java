package skcc.arch.code.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import skcc.arch.code.infrastructure.jpa.CodeEntity;

@Getter
@Builder
public class CodeSearchCondition {

    private String code;
    private String codeName;
    private String description;
    private Long parentCodeId;
    @Setter
    private CodeEntity parentCode;
    private Boolean delYn;
}
