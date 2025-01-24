package skcc.arch.code.infrastructure.jpa;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import skcc.arch.code.domain.Code;
import skcc.arch.code.domain.CodeCreateRequest;
import skcc.arch.code.domain.CodeSearchCondition;
import skcc.arch.code.service.dto.CodeDto;
import skcc.arch.code.service.dto.QCodeDto;
import skcc.arch.common.infrastructure.jpa.JpaConfig;

import java.util.List;
import java.util.stream.Collectors;

import static skcc.arch.code.infrastructure.jpa.QCodeEntity.codeEntity;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("/sql/code-repository-test-data.sql")
@Slf4j
@Import(JpaConfig.class)
class CodeRepositoryJpaCustomImplTest {

    @Autowired
    private CodeRepositoryJpa repositoryJpa;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private EntityManager entityManager;


    @Test
    void N_플러스_1_발생() throws Exception {

        long start = System.currentTimeMillis();

        List<CodeEntity> parents = queryFactory
                .selectFrom(codeEntity)
                .where(codeEntity.parentCode.isNull()) // 부모 노드만 조회
                .fetch();

        for (CodeEntity parent : parents) {
            System.out.println("Parent Code: " + parent.getCode());  // 부모 정보 출력

            // 자식 데이터 조회 시 Lazy 로딩 발생
            for (CodeEntity child : parent.getChild()) {
                System.out.println("   Child Code: " + child.getCode());
            }
        }
        log.info("Time : {}", System.currentTimeMillis() - start);
    }


    @Test
    void 페치조인_사용() throws Exception {
        long start = System.currentTimeMillis();

        List<CodeEntity> parents = queryFactory
                .selectFrom(codeEntity)
                .leftJoin(codeEntity.child).fetchJoin() // 자식 데이터를 함께 가져옴
                .where(codeEntity.parentCode.isNull())
                .fetch();

        for (CodeEntity parent : parents) {
            System.out.println("Parent Code: " + parent.getCode());

            // 이미 자식을 로드했으므로 추가 쿼리가 발생하지 않음
            for (CodeEntity child : parent.getChild()) {
                System.out.println("   Child Code: " + child.getCode());
            }
        }
        log.info("Time : {}", System.currentTimeMillis() - start);
    }

    @Test
    void 페치조인_컨디션() throws Exception {
        //given
        CodeSearchCondition condition = CodeSearchCondition.builder()
                .code("ROOT2")
                .build();

        //when
        List<CodeDto> list = queryFactory
                .selectFrom(codeEntity)
                .leftJoin(codeEntity.child).fetchJoin() // 하위 자식식
                .where(CodeConditionBuilder.codeCondition(condition))
                .fetch()
                .stream()
                .map(CodeEntity::toDto) // 3단계부터는 개별 조회
                .toList();

        log.info("list : {}", list);


    }


    @Test
    void findByCode() throws Exception {
        //given
        CodeSearchCondition condition = CodeSearchCondition.builder()
                .code("A001")
                .build();
        
        // Map the results using QCodeDto with the specified fields
        List<CodeDto> resultEntities = queryFactory
                .select(new QCodeDto(
                        codeEntity.id,
                        codeEntity.code,
                        codeEntity.codeName,
                        codeEntity.parentCode.id,
                        codeEntity.seq,
                        codeEntity.description,
                        codeEntity.delYn,
                        codeEntity.createdDate,
                        codeEntity.lastModifiedDate
                ))
                .from(codeEntity)
                .leftJoin(codeEntity.child).fetchJoin() // 자식 데이터를 함께 가져옴
                .where(CodeConditionBuilder.codeCondition(condition))
                .fetch();


        log.info("fetch : {}", resultEntities);
//                .leftJoin(codeEntity, QCodeEntity.codeEntity).fetchJoin() // 자식 코드 fetch join
//                .where(codeEntity.code.eq("A001")) // 조건 추가
//                .fetch();
//
//        if(result != null ) {
//            result.setChild(findChildCodes(result.getId()));
//        }
//
//        when
//        log.info("result : {}", result);


        //then
//        Assertions.assertThat(result.getChild()).isNotEmpty();
//        Assertions.assertThat(result.getChild().size()).isGreaterThan(1);
        // result.getCode().equals("A001"));

    }

    @Test
    void findByCodeWithChild() {
        //given
        CodeSearchCondition condition = CodeSearchCondition.builder()
                .code("A001")
                .build();

        List<CodeEntity> all = repositoryJpa.findAll();
        log.info("all : {}", all);
        log.info("all.size() : {}", all.size());

        // 기준 엔티티 조회
        List<CodeEntity> result = queryFactory
                .selectFrom(codeEntity)
                .where(codeEntity.code.eq(condition.getCode()))
                .fetch();

        // 자식 조회
        List<CodeDto> list = result.stream()
                .map(parent -> CodeDto.builder()
                        .id(parent.getId())
                        .code(parent.getCode())
                        .codeName(parent.getCodeName())
                        .description(parent.getDescription())
                        .seq(parent.getSeq())
                        .parentCodeId(parent.getParentCode() != null ? parent.getParentCode().getId() : null)
                        .child(findChildCodes(parent.getId()))
                        .build())
                .toList();

        log.info("list : {}", list);
    }

    private List<CodeDto> findChildCodes(Long parentId) {
        // 자식 코드 조회
        List<CodeDto> children = queryFactory.selectFrom(codeEntity)
                .where(codeEntity.parentCode.id.eq(parentId))
                .fetch()
                .stream()
                .map(CodeEntity::toDto)
                .toList();

        // 각 자식 코드를 기준으로 하위 계층 구조를 계속 탐색 (재귀 호출)
        for (CodeDto child : children) {
            child.setChild(findChildCodes(child.getId()));
        }

        return children;
    }
}