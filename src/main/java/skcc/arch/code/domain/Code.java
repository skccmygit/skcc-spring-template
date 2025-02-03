package skcc.arch.code.domain;

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

    public static Code from (CodeCreateRequest codeCreateRequest) {
        return Code.builder()
                .code(codeCreateRequest.getCode())
                .codeName(codeCreateRequest.getCodeName())
                .parentCodeId(codeCreateRequest.getParentCodeId())
                .seq(codeCreateRequest.getSeq())
                .description(codeCreateRequest.getDescription())
                .delYn(false)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }

    public Code update(CodeUpdateRequest codeUpdateRequest) {
        return Code.builder()
                .id(id)
                .createdDate(createdDate)
                .code(codeUpdateRequest.getCode())
                .codeName(codeUpdateRequest.getCodeName())
                .parentCodeId(codeUpdateRequest.getParentCodeId())
                .seq(codeUpdateRequest.getSeq())
                .description(codeUpdateRequest.getDescription())
                .lastModifiedDate(LocalDateTime.now())
                .delYn(codeUpdateRequest.isDelYn())
                .build();
    }

}
