package skcc.arch.biz.code.infrastructure.jpa;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.util.ObjectUtils;

import static org.springframework.util.StringUtils.hasText;
import static skcc.arch.biz.code.infrastructure.jpa.QCodeEntity.codeEntity;

public abstract class CodeConditionBuilder {

    public static BooleanBuilder codeCondition(CodeSearchCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();
        if (condition == null) {
            return builder.and(alwaysTrue());
        }

        return builder
                .and(codeEq(condition.getCode()))
                .and(codeNameLike(condition.getCodeName()))
                .and(descriptionLike(condition.getDescription()))
                .and(parentCodeEq(condition.getParentCode()))
                .and(delYnEq(condition.getDelYn()))
                ;
    }

    private static BooleanExpression codeEq(String code) {
        return hasText(code) ? codeEntity.code.eq(code) : null;
    }

    private static BooleanExpression codeNameLike(String codeName) {
        return hasText(codeName) ? codeEntity.codeName.like("%" + codeName + "%") : null;
    }

    private static BooleanExpression descriptionLike(String description) {
        return hasText(description) ? codeEntity.description.like("%" + description + "%") : null;
    }

    private static BooleanExpression parentCodeEq(CodeEntity parentCode) {
        return ObjectUtils.isEmpty(parentCode) ? null : codeEntity.parentCode.eq(parentCode);
    }

    // DEL_YN의 경우 기본값이 FALSE
    private static BooleanExpression delYnEq(Boolean delYn) {
        return codeEntity.delYn.eq(Boolean.TRUE.equals(delYn));
    }

    private static BooleanExpression alwaysTrue() {
        return codeEntity.isNotNull(); // 항상 참인 조건으로 설정.
    }
}
