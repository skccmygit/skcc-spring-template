package skcc.arch.code.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import skcc.arch.code.domain.Code;
import skcc.arch.code.service.dto.CodeDto;

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
        return CodeResponse.builder()
                .id(code.getId())
                .code(code.getCode())
                .codeName(code.getCodeName())
                .parentCodeId(code.getParentCodeId())
                .seq(code.getSeq())
                .description(code.getDescription())
                .delYn(code.isDelYn())
                .build();
    }

    public static CodeResponse from(CodeDto codeDto) {
        return CodeResponse.builder()
                .id(codeDto.getId())
                .code(codeDto.getCode())
                .codeName(codeDto.getCodeName())
                .parentCodeId(codeDto.getParentCodeId())
                .child(codeDto.getChild() == null ? null : codeDto.getChild().stream().map(CodeResponse::from).toList())
                .seq(codeDto.getSeq())
                .description(codeDto.getDescription())
                .delYn(codeDto.isDelYn())
                .build();
    }

}
