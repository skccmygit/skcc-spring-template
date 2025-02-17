package skcc.arch.biz.code.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import skcc.arch.biz.code.domain.Code;

import java.util.List;


@Getter
@Builder
public class CodeResponse {

    private Long id;
    private String code;
    private String codeName;
    private Long parentCodeId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CodeResponse> child;
    private int seq;
    private String description;
    private boolean delYn;

    public static CodeResponse from(Code code) {
        if(code == null) return null;
        return CodeResponse.builder()
                .id(code.getId())
                .code(code.getCode())
                .codeName(code.getCodeName())
                .parentCodeId(code.getParentCodeId())
                .child(code.getChild() == null ? null : code.getChild().stream().map(CodeResponse::from).toList())
                .seq(code.getSeq())
                .description(code.getDescription())
                .delYn(code.isDelYn())
                .build();
    }

}
