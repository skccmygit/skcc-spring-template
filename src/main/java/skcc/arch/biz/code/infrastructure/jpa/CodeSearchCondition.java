package skcc.arch.biz.code.infrastructure.jpa;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import skcc.arch.biz.code.domain.CodeSearch;

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

    public static CodeSearchCondition from (CodeSearch codeSearch) {
        return CodeSearchCondition.builder()
                .code(codeSearch.getCode())
                .codeName(codeSearch.getCodeName())
                .parentCodeId(codeSearch.getParentCodeId())
                .description(codeSearch.getDescription())
                .delYn(codeSearch.getDelYn())
                .build();
    }
}
