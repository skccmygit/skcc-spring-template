package skcc.arch.code.infrastructure.jpa;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import skcc.arch.code.domain.Code;
import skcc.arch.code.domain.CodeSearchCondition;
import skcc.arch.code.service.port.CodeRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static skcc.arch.code.infrastructure.jpa.QCodeEntity.codeEntity;

@Repository
@RequiredArgsConstructor
public class CodeRepositoryJpaCustomImpl implements CodeRepository {

    private final CodeRepositoryJpa codeRepositoryJpa;
    private final JPAQueryFactory queryFactory;

    @Override
    public Code save(Code code) {
        CodeEntity parentCodeEntity = getParentCodeEntity(code.getParentCodeId());
        CodeEntity savedCode = codeRepositoryJpa.save(CodeEntity.from(code, parentCodeEntity));
        return savedCode.toModel();
    }

    @Override
    public Optional<Code> findById(Long id) {
        return codeRepositoryJpa.findById(id)
                .map(CodeEntity::toModel);
    }

    @Override
    public Optional<Code> findByIdWithChild(Long id) {
        return Optional.ofNullable(
                queryFactory.selectFrom(codeEntity)
                        .leftJoin(codeEntity.child).fetchJoin()
                        .where(codeEntity.id.eq(id))
                        .fetchOne()
        ).map(CodeEntity::toModelWithChild);
    }

    @Override
    public Page<Code> findByCondition(Pageable pageable, CodeSearchCondition condition) {
        List<Code> content = getQueryResults(pageable, condition, false);
        Long totalCount = getTotalCount(condition);
        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public Page<Code> findByConditionWithChild(Pageable pageable, CodeSearchCondition condition) {
        List<Code> content = getQueryResults(pageable, condition, true);
        Long totalCount = getTotalCount(condition);
        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public Optional<Code> findTopByParentCodeIdOrderBySeqDesc(Long parentCodeId) {
        return codeRepositoryJpa.findTopByParentCodeIdOrderBySeqDesc(parentCodeId).map(CodeEntity::toModel);
    }

    @Override
    public boolean existsCodeEntityByParentCodeIdAndSeqOrderBySeqDesc(Long parentCodeId, Integer seq) {
        return codeRepositoryJpa.existsCodeEntityByParentCodeIdAndSeqOrderBySeqDesc(parentCodeId, seq);
    }

    @Override
    public Code update(Code code) {
        CodeEntity codeEntity = codeRepositoryJpa.findById(code.getId()).orElse(null);

        return null;
    }

    private CodeEntity getParentCodeEntity(Long parentCodeId) {
        if (parentCodeId == null) {
            return null;
        }
        return codeRepositoryJpa.findById(parentCodeId).orElse(null);
    }

    private List<Code> getQueryResults(Pageable pageable, CodeSearchCondition condition, boolean withChild) {
        var query = queryFactory.selectFrom(codeEntity)
                .where(CodeConditionBuilder.codeCondition(condition))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        if (withChild) {
            query.leftJoin(codeEntity.child).fetchJoin();
        }
        return query.fetch().stream()
                .map(withChild ? CodeEntity::toModelWithChild : CodeEntity::toModel)
                .collect(Collectors.toList());
    }

    private Long getTotalCount(CodeSearchCondition condition) {
        return queryFactory
                .select(codeEntity.count())
                .from(codeEntity)
                .where(CodeConditionBuilder.codeCondition(condition))
                .fetchOne();
    }

}
