package skcc.arch.code.domain;

import lombok.*;

@Getter
@Builder
@ToString
public class CodeCreateRequest {

    private final String code;
    private final String codeName;
    private final Long parentCodeId;
    @Setter
    private int seq;
    private final String description;

}
