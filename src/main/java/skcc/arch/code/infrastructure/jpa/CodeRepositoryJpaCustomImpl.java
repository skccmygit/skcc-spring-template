package skcc.arch.code.infrastructure.jpa;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import skcc.arch.code.domain.Code;
import skcc.arch.code.domain.CodeSearchCondition;
import skcc.arch.code.service.dto.CodeDto;
import skcc.arch.code.service.port.CodeRepository;

import java.util.Optional;

import static skcc.arch.code.infrastructure.jpa.QCodeEntity.codeEntity;

@Repository
@RequiredArgsConstructor
public class CodeRepositoryJpaCustomImpl implements CodeRepository {

    private final CodeRepositoryJpa codeRepositoryJpa;
    private final JPAQueryFactory queryFactory;

    @Override
    public Code save(Code code) {

        Long parentCodeId = code.getParentCodeId();
        CodeEntity parentCodeEntity = null;
        if(parentCodeId != null) {
            parentCodeEntity = codeRepositoryJpa.findById(parentCodeId).orElse(null);
        }
        CodeEntity savedCode = codeRepositoryJpa.save(CodeEntity.from(code, parentCodeEntity));
        return savedCode.toModel();
    }

    @Override
    public Optional<Code> findById(Long id) {
        return codeRepositoryJpa.findById(id)
                .map(CodeEntity::toModel);
    }

    @Override
    public Optional<CodeDto> findByIdWithChild(Long id) {

        return Optional.ofNullable(
                queryFactory.selectFrom(codeEntity)
                        .leftJoin(codeEntity.child).fetchJoin() // 자식 데이터를 함께 가져옴
                        .where(codeEntity.id.eq(id))
                        .fetchOne()
        ).map(CodeEntity::toDto);
    }

    @Override
    public Optional<Code> findByCode(String code) {
        return codeRepositoryJpa.findByCode(code)
                .map(CodeEntity::toModel);
    }

    @Override
    public Optional<CodeDto> findByCodeWithChild(CodeSearchCondition condition) {
        return queryFactory
                .selectFrom(codeEntity)
                .leftJoin(codeEntity.child).fetchJoin() // 자식 데이터를 함께 가져옴
                .where(CodeConditionBuilder.codeCondition(condition))
                .fetch().stream()
                .map(CodeEntity::toDto)
                .findFirst();
    }

    @Override
    public Page<Code> findAll(Pageable pageable) {
        return codeRepositoryJpa.findAll(pageable)
                .map(CodeEntity::toModel);
    }

//
//    private List<CodeDto> findChildCodes(Long parentId) {
//        // 자식 코드 조회
//        List<CodeDto> children = queryFactory.selectFrom(codeEntity)
//                .where(codeEntity.parentCode.id.eq(parentId))
//                .fetch()
//                .stream()
//                .map(CodeEntity::toDto)
//                .toList();
//
//        // 각 자식 코드를 기준으로 하위 계층 구조를 계속 탐색 (재귀 호출)
//        for (CodeDto child : children) {
//            child.setChild(findChildCodes(child.getId()));
//        }
//
//        return children;
//    }
}
