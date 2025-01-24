package skcc.arch.code.service.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CodeDto{

    private Long id;
    private String code;
    private String codeName;
    private Long parentCodeId;
    private List<CodeDto> child;
    private int seq;
    private String description;
    private boolean delYn;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    @Builder
    public CodeDto(Long id, String code, String codeName, Long parentCodeId, List<CodeDto> child, int seq, String description, boolean delYn, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        this.id = id;
        this.code = code;
        this.codeName = codeName;
        this.parentCodeId = parentCodeId;
        this.child = child;
        this.seq = seq;
        this.description = description;
        this.delYn = delYn;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

}
