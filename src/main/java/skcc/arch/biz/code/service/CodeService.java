package skcc.arch.biz.code.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import skcc.arch.app.exception.CustomException;
import skcc.arch.app.exception.ErrorCode;
import skcc.arch.biz.code.controller.port.CodeServicePort;
import skcc.arch.biz.code.domain.Code;
import skcc.arch.biz.code.domain.CodeCreate;
import skcc.arch.biz.code.domain.CodeSearch;
import skcc.arch.biz.code.domain.CodeUpdate;
import skcc.arch.biz.code.service.port.CodeRepositoryPort;
import skcc.arch.biz.common.constants.CacheGroup;
import skcc.arch.biz.common.service.MyCacheService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeService implements CodeServicePort {

    private final MyCacheService myCacheService;
    private final CodeRepositoryPort codeRepositoryPort;

    @Override
    @Transactional
    public Code save(CodeCreate codeCreate) {

        // 존재하는 코드인지
        validateExistCode(codeCreate.toSearch());

        // 부모코드가 유효한 코드인지
        if (codeCreate.getParentCodeId() != null) {
            findByCodeId(codeCreate.getParentCodeId());
        }

        // DB에서 마지막 순번 구하기
        int inputSeq = calcNewSeq(codeCreate.getSeq(), codeCreate.getParentCodeId());

        CodeCreate setSeqModel = CodeCreate.builder()
                .code(codeCreate.getCode())
                .codeName(codeCreate.getCodeName())
                .parentCodeId(codeCreate.getParentCodeId())
                .seq(inputSeq)
                .build();

        Code savedCode = codeRepositoryPort.save(Code.from(setSeqModel));

        // 캐시 데이터 수정
        cacheUpdate(findByParentCode(savedCode));

        return savedCode;
    }

    /**
     * 코드 신규 저장시 SEQ 값을 생성한다
     * 입력받은 seq, parentCodeId 정보로 **seq** 계산
     *   입력받은 seq 값이 없을 경우 DB의 마지막 값 + 1
     *                  있을 경우 존재유무 파악
     *   존재시, 입력값 + 1로 재조회(반복)
     *   미존재시, 입력값으로 세팅
     */
    private int calcNewSeq(int seq, Long parentCodeId) {
        int inputSeq;
        if(seq == 0 ) {
            inputSeq = getLastSeq(parentCodeId);
        }else {
            inputSeq = seq;
            // 입력순서가 존재하면 + 1
            while (codeRepositoryPort.existsCodeEntityByParentCodeIdAndSeqOrderBySeqDesc(parentCodeId, inputSeq)) {
                inputSeq++;
            }
        }
        return inputSeq;
    }

    private int getLastSeq(Long parentCodeId) {
        int lastSeq;
        Optional<Code> result = codeRepositoryPort.findTopByParentCodeIdOrderBySeqDesc(parentCodeId);
        lastSeq = result.map(Code::getSeq).orElse(0) + 1;
        return lastSeq;
    }


    @Override
    public Code findById(Long id) {
        return codeRepositoryPort.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT)
        );
    }

    @Override
    public Code findByIdWithChild(Long id) {
        return codeRepositoryPort.findByIdWithChild(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT)
        );
    }

    @Override
    public Page<Code> findByCode(Pageable pageable, CodeSearch codeSearch) {
        return codeRepositoryPort.findByCondition(pageable, codeSearch);
    }

    @Override
    public Page<Code> findByConditionWithChild(Pageable pageable, CodeSearch codeSearch) {
        return codeRepositoryPort.findByConditionWithChild(pageable, codeSearch);
    }

    @Override
    @Transactional
    public Code update(CodeUpdate codeUpdate) {


        // 부모ID가 null이 아닌경우 부모객체 존재 확인
        if (codeUpdate.getParentCodeId() != null) {
            findByCodeId(codeUpdate.getParentCodeId());
        }

        // 업데이트 대상 엔티티 조회
        Code code = findByCodeId(codeUpdate.getId());

        // 도메인 모델 업데이트 비즈니스 로직 수행 (하위->상위로 변경되었을 경우 하위객체의 순번은 조정하지 않는다)
        code = code.update(codeUpdate);

        // DB 업데이트
        Code updated = codeRepositoryPort.update(code);

        // 형제 순번 조정
        reorderSequence(updated.getId(), updated.getSeq(), updated.getParentCodeId());

        // 캐시 데이터 수정
        cacheUpdate(findByParentCode(updated));

        return updated;
    }

    @Override
    public Code findByCode(CodeSearch codeSearch) {
        if (codeSearch.getCode() != null) {

            // 캐시 조회
            Code cachedCode = myCacheService.get(CacheGroup.CODE, codeSearch.getCode(), Code.class);
            if (cachedCode != null) {
                return cachedCode;
            }

            // DB 조회
            Code dbCode = codeRepositoryPort.findByCode(codeSearch);

            // 루트 요소일 경우 캐시 추가
            if (dbCode != null && dbCode.getParentCodeId() == null) {
                myCacheService.put(CacheGroup.CODE, codeSearch.getCode(), dbCode);
            }
            return dbCode;
        }
        return null;
    }

    private void reorderSequence(Long codeId, int seq, Long parentCodeId) {
        boolean existsed = codeRepositoryPort.existsCodeEntityByParentCodeIdAndSeqOrderBySeqDesc(parentCodeId, seq);
        if(existsed) {
            List<Code> childList;
            // ROOT 인 경우
            if (parentCodeId == null) {
                childList = codeRepositoryPort.findByParentCodeId(parentCodeId);
            }
            // 부모가 있는 경우
            else {
                Code parent = codeRepositoryPort.findByIdWithChild(parentCodeId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT));
                childList = parent.getChild();
            }
            // 자식 순서 조정 (본인 제외)
            updateSeqItems(seq, childList.stream().filter(c -> !c.getId().equals(codeId)).toList());
        }

    }

    /**
     * 순번은 요청 객체가 우선적으로 점유하며
     * 중복일 경우 +1 증가하여 조정한다
     */
    private void updateSeqItems(int seq, List<Code> items) {
        // 본인보다 큰 SEQ 리스트만 필터
        List<Code> childList = items.stream()
                .filter(c -> c.getSeq() >= seq )
                .toList();

        int indexSeq = seq;

        for (Code code : childList) {
            if (code.getSeq() == indexSeq || code.getSeq() == seq) {
                // 중복되지 않도록 순번 증가
                Code updateCode = code.changeSeq(++indexSeq);
                // SEQ가 변경된 객체 업데이트
                codeRepositoryPort.save(updateCode);
            }
        }
    }

    private Code findByCodeId(Long codeId) {
        return codeRepositoryPort.findById(codeId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT)
        );
    }

    private void validateExistCode(CodeSearch codeSearch) {
        Page<Code> result = codeRepositoryPort.findByCondition(PageRequest.of(0, 10), codeSearch);
        if (!result.getContent().isEmpty()) {
            throw new CustomException(ErrorCode.EXIST_ELEMENT);
        }
    }

    private Code findByParentCode(Code code) {
        if(code.getParentCodeId() == null) {
            return code;
        } else {
            Code result = findByCodeId(code.getParentCodeId());
            return findByParentCode(result);
        }
    }

    private void cacheUpdate(Code code) {
        CodeSearch codeSearch = CodeSearch.builder()
                .code(code.getCode())
                .build();
        Code result = codeRepositoryPort.findByCode(codeSearch);

        myCacheService.put(CacheGroup.CODE, code.getCode(), result);
    }

}
