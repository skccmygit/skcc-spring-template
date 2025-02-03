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
            validateExistParentCodeId(codeCreateRequest.getParentCodeId());
        }

        // DB에서 마지막 순번 구하기
        int inputSeq = calcSeq(codeCreateRequest);
        codeCreateRequest.setSeq(inputSeq);

        return codeRepository.save(Code.from(codeCreateRequest));
    }

    /**
     * Seq 정보를 계산
     *   입력값이 없을 경우 DB의 마지막 값 + 1
     *   입력값이 있을 경우 존재유무 파악
     *      존재시, 입력값 + 1로 재조회(반복)
     *      미존재시, 입력값으로 세팅
     * @param codeCreateRequest
     * @return
     */
    private int calcSeq(CodeCreateRequest codeCreateRequest) {
        int inputSeq;
        if(codeCreateRequest.getSeq() == 0 ) {
            inputSeq = getLastSeq(codeCreateRequest);
        }else {
            inputSeq = codeCreateRequest.getSeq();
            // 입력순서가 존재하면 + 1
            while (codeRepository.existsCodeEntityByParentCodeIdAndSeqOrderBySeqDesc(codeCreateRequest.getParentCodeId(), inputSeq)) {
                inputSeq++;
            }
        }
        return inputSeq;
    }

    private int getLastSeq(CodeCreateRequest codeCreateRequest) {
        int lastSeq;
        Optional<Code> result = codeRepository.findTopByParentCodeIdOrderBySeqDesc(codeCreateRequest.getParentCodeId());
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

        // 엔티티가 존재하는지
        Code code = codeRepository.findById(codeUpdateRequest.getId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT)
        );

        // TODO - Validation Check
        validateUpdate(codeUpdateRequest);
        log.info("변경전 code : {}", code);
        code = code.update(codeUpdateRequest);
        log.info("변경후 code : {}", code);
        return codeRepository.save(code);
    }

    private void validateUpdate(CodeUpdateRequest codeUpdateRequest) {
        // 자신의 ID와 부모의 ID는 같을 수 없음
        if (codeUpdateRequest.getId().equals(codeUpdateRequest.getParentCodeId())) {
            throw new IllegalStateException("자신의 ID와 부모ID는 같을 수 없습니다. ");
        }

        if (codeUpdateRequest.getParentCodeId() != null) {
            validateExistParentCodeId(codeUpdateRequest.getParentCodeId());
        }
        // 순번 조정

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

    private void validateExistParentCodeId(Long parentCodeId) {
        Optional<Code> code = codeRepository.findById(parentCodeId);
        if (code.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_ELEMENT);
        }
    }

}
