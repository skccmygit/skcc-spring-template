package skcc.arch.biz.code.controller.request;

import lombok.Builder;
import lombok.Getter;
import skcc.arch.biz.code.domain.CodeSearch;

@Getter
@Builder
public class CodeSearchRequest {

    private final String code;
    private final String codeName;
    private final String description;
    private final Long parentCodeId;
    private final Boolean delYn;

    public CodeSearch toModel() {
        return CodeSearch.builder()
                .code(code)
                .codeName(codeName)
                .description(description)
                .parentCodeId(parentCodeId)
                .delYn(delYn)
                .build();
    }
}
