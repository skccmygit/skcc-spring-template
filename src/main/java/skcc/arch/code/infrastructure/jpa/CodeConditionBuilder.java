package skcc.arch.code.infrastructure.jpa;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import skcc.arch.code.domain.CodeSearchCondition;

import static org.springframework.util.StringUtils.hasText;

public abstract class CodeConditionBuilder {

    public static BooleanBuilder codeCondition(CodeSearchCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();
        if (condition == null) {
            return builder.and(alwaysTrue());
        }
        return builder
                .and(codeEq(condition.getCode()))
                .and(codeNameLike(condition.getCodeName()));
    }

    private static BooleanExpression codeEq(String code) {
        return hasText(code) ? QCodeEntity.codeEntity.code.eq(code) : null;
    }
    private static BooleanExpression codeNameLike(String codeName) {
        return hasText(codeName) ? QCodeEntity.codeEntity.codeName.like("%"+codeName+"%") : null;
    }

    private static BooleanExpression alwaysTrue() {
        return QCodeEntity.codeEntity.isNotNull(); // 항상 참인 조건으로 설정.
    }
}
