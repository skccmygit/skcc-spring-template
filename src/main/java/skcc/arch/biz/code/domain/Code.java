package skcc.arch.biz.code.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class Code {

    private final Long id;
    private final String code;
    private final String codeName;
    private final Long parentCodeId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<Code> child;
    private final int seq;
    private final String description;
    private final boolean delYn;
    private final LocalDateTime createdDate;
    private final LocalDateTime lastModifiedDate;

    public static Code from(CodeCreate codeCreate) {
        return Code.builder()
                .code(codeCreate.getCode())
                .codeName(codeCreate.getCodeName())
                .parentCodeId(codeCreate.getParentCodeId())
                .seq(codeCreate.getSeq())
                .description(codeCreate.getDescription())
                .delYn(false)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }


    public Code update(CodeUpdate codeUpdate) {
        return Code.builder()
                .id(id) //변경되지 않은값
                .createdDate(createdDate)
                .code(codeUpdate.getCode())
                .codeName(codeUpdate.getCodeName())
                .parentCodeId(codeUpdate.getParentCodeId())
                .seq(codeUpdate.getSeq())
                .description(codeUpdate.getDescription())
                .delYn(codeUpdate.isDelYn())
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }

    /**
     * 순번만 변경
     */
    public Code changeSeq(int seq) {
        return Code.builder()
                .id(id)
                .code(code)
                .codeName(codeName)
                .parentCodeId(parentCodeId)
                .child(child)
                .seq(seq)
                .description(description)
                .delYn(delYn)
                .createdDate(createdDate)
                .lastModifiedDate(lastModifiedDate)
                .build();
    }

}
