package skcc.arch.code.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CodeCreate {

    private final String code;
    private final String codeName;
    private final Long parentCodeId;
    private final int seq;
    private final String description;
}
