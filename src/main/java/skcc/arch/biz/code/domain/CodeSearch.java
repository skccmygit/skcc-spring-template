package skcc.arch.biz.code.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CodeSearch {
    private final String code;
    private final String codeName;
    private final String description;
    private final Long parentCodeId;
    private final Boolean delYn;
}
