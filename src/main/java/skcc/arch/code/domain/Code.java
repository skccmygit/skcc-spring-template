package skcc.arch.code.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class Code {

    private final Long id;
    private final String code;
    private final String codeName;
    private final Long parentCodeId;
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

}
