package skcc.arch.code.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@RequiredArgsConstructor
@ToString
public class CodeCreateRequest {

    private final String code;
    private final String codeName;
    private final Long parentCodeId;
    private final int seq;
    private final String description;

}
