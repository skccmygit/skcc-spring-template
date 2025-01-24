package skcc.arch.code.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CodeSearchCondition {

    private String code;
    private String codeName;
    private Long parentCodeId;
    private String description;
    private Boolean delYn;
}
