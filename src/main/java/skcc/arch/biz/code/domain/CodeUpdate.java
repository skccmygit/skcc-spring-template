package skcc.arch.biz.code.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CodeUpdate {

    private final Long id;
    private final String code;
    private final String codeName;
    private final int seq;
    private final Long parentCodeId;
    private final String description;
    private final boolean delYn;

    @Builder
    public CodeUpdate(Long id, String code, String codeName, int seq, Long parentCodeId, String description, boolean delYn) {
        // 자신의 ID와 부모의 ID는 같을 수 없음
        if (id.equals(parentCodeId)) {
            throw new IllegalStateException("자신의 ID와 부모ID는 같을 수 없습니다.");
        }

        this.id = id;
        this.code = code;
        this.codeName = codeName;
        this.seq = seq;
        this.parentCodeId = parentCodeId;
        this.description = description;
        this.delYn = delYn;
    }
}
