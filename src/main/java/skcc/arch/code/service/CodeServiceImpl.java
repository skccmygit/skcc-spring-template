package skcc.arch.code.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import skcc.arch.app.exception.CustomException;
import skcc.arch.app.exception.ErrorCode;
import skcc.arch.code.controller.port.CodeService;
import skcc.arch.code.domain.Code;
import skcc.arch.code.domain.CodeCreateRequest;
import skcc.arch.code.domain.CodeSearchCondition;
import skcc.arch.code.domain.CodeUpdateRequest;
import skcc.arch.code.service.port.CodeRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeServiceImpl implements CodeService {

    private final CodeRepository codeRepository;

    @Override
    @Transactional
    public Code save(CodeCreateRequest codeCreateRequest) {

        // 존재하는 코드인지
        validateExistCode(codeCreateRequest);
        // 부모코드가 유효한 코드인지
        if (codeCreateRequest.getParentCodeId() != null) {
            validateCodeId(codeCreateRequest.getParentCodeId());
        }

        // DB에서 마지막 순번 구하기
        int inputSeq = calcNewSeq(codeCreateRequest.getSeq(), codeCreateRequest.getParentCodeId());
        codeCreateRequest.setSeq(inputSeq);

        return codeRepository.save(Code.from(codeCreateRequest));
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
            while (codeRepository.existsCodeEntityByParentCodeIdAndSeqOrderBySeqDesc(parentCodeId, inputSeq)) {
                inputSeq++;
            }
        }
        return inputSeq;
    }

    private int getLastSeq(Long parentCodeId) {
        int lastSeq;
        Optional<Code> result = codeRepository.findTopByParentCodeIdOrderBySeqDesc(parentCodeId);
        lastSeq = result.map(Code::getSeq).orElse(0) + 1;
        return lastSeq;
    }


    @Override
    public Code findById(Long id) {
        return codeRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT)
        );
    }

    @Override
    public Code findByIdWithChild(Long id) {
        return codeRepository.findByIdWithChild(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT)
        );
    }

    @Override
    public Page<Code> findByCode(Pageable pageable, CodeSearchCondition condition) {
        return codeRepository.findByCondition(pageable, condition);
    }

    @Override
    public Page<Code> findByCodeWithChild(Pageable pageable, CodeSearchCondition condition) {
        return codeRepository.findByConditionWithChild(pageable, condition);
    }

    @Override
    @Transactional
    public Code update(CodeUpdateRequest codeUpdateRequest) {

        // 유효성 검증
        validateUpdateRequest(codeUpdateRequest);

        // 업데이트 대상 엔티티 조회
        Code updateCode = codeRepository.findById(codeUpdateRequest.getId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT)
        );

        // 도메인 모델 업데이트 비즈니스 로직 수행
        updateCode = updateCode.update(codeUpdateRequest);

        // 형제 순번 조정
        reorderSequence(codeUpdateRequest.getId(), codeUpdateRequest.getSeq(), codeUpdateRequest.getParentCodeId());

        // EntityManager 수행
        return codeRepository.save(updateCode);
    }


    private void reorderSequence(Long codeId, int seq, Long parentCodeId) {
        boolean existsed = codeRepository.existsCodeEntityByParentCodeIdAndSeqOrderBySeqDesc(parentCodeId, seq);
        if(existsed) {
            List<Code> childList;
            // ROOT 인 경우
            if (parentCodeId == null) {
                Page<Code> root = codeRepository.findByConditionWithChild(PageRequest.of(0, 1000), CodeSearchCondition.builder().parentCodeId(parentCodeId).build());
                childList = root.getContent();
            }
            // 부모가 있는 경우
            else {
                Code parent = codeRepository.findByIdWithChild(parentCodeId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT));
                childList = parent.getChild();
            }
            // 자식 순서 조정
            updateSeqItems(codeId, seq, childList);
        }

    }

    private void updateSeqItems(Long codeId, int seq, List<Code> items) {
        // 본인보다 큰 SEQ 리스트만 필터 (본인제외)
        List<Code> childList = items.stream()
                .filter(c -> c.getSeq() >= seq && !c.getId().equals(codeId))
                .toList();

        int indexSeq = seq;

        for (Code code : childList) {
            if (code.getSeq() == indexSeq || code.getSeq() == seq) {
                // 중복되지 않도록 순번 증가
                Code updateCode = code.changeSeq(indexSeq++);
                // SEQ가 변경된 객체 업데이트
                codeRepository.save(updateCode);
            }
        }
    }

    private void validateUpdateRequest(CodeUpdateRequest codeUpdateRequest) {
        // 자신의 ID와 부모의 ID는 같을 수 없음
        if (codeUpdateRequest.getId().equals(codeUpdateRequest.getParentCodeId())) {
            throw new IllegalStateException("자신의 ID와 부모ID는 같을 수 없습니다.");
        }

        // 부모ID가 존재하는지 확인
        if (codeUpdateRequest.getParentCodeId() != null) {
            validateCodeId(codeUpdateRequest.getParentCodeId());
        }
    }

    private void validateCodeId(Long codeId) {
        codeRepository.findById(codeId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT)
        );
    }

    private void validateExistCode(CodeCreateRequest codeCreateRequest) {
        CodeSearchCondition condition = CodeSearchCondition.builder()
                .code(codeCreateRequest.getCode())
                .build();
        Page<Code> result = codeRepository.findByCondition(PageRequest.of(0, 10), condition);
        if (!result.getContent().isEmpty()) {
            throw new CustomException(ErrorCode.EXIST_ELEMENT);
        }
    }

}
