package skcc.arch.code.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import skcc.arch.app.exception.CustomException;
import skcc.arch.app.exception.ErrorCode;
import skcc.arch.code.controller.port.CodeService;
import skcc.arch.code.domain.Code;
import skcc.arch.code.domain.CodeCreateRequest;
import skcc.arch.code.domain.CodeSearchCondition;
import skcc.arch.code.service.dto.CodeDto;
import skcc.arch.code.service.port.CodeRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService {

    private final CodeRepository codeRepository;

    @Override
    public Code save(CodeCreateRequest codeCreateRequest) {

        // 존재하는 코드인지
        checkExistCode(codeCreateRequest);
        // 부모코드가 유효한 코드인지
        if(codeCreateRequest.getParentCodeId() != null) {
            checkExistParentCode(codeCreateRequest.getParentCodeId());
        }
        return codeRepository.save(Code.from(codeCreateRequest));
    }


    @Override
    public Code findById(Long id) {
        return codeRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT)
        );
    }

    @Override
    public CodeDto findByIdWithChild(Long id) {
        return codeRepository.findByIdWithChild(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT)
        );
    }

    @Override
    public Code findByCode(String code) {
        return null;
    }

    @Override
    public Page<Code> findAll(Pageable pageable) {
        return codeRepository.findAll(pageable);
    }

    @Override
    public CodeDto findByCodeWithChild(CodeSearchCondition condition) {
        Optional<CodeDto> result = codeRepository.findByCodeWithChild(condition);
        return result.orElse(null);
    }

    private void checkExistCode(CodeCreateRequest codeCreateRequest) {
        Optional<Code> code = codeRepository.findByCode(codeCreateRequest.getCode());
        if (code.isPresent()) {
            throw new CustomException(ErrorCode.EXIST_ELEMENT);
        }
    }

    private void checkExistParentCode(Long parentCodeId) {
        Optional<Code> code = codeRepository.findById(parentCodeId);
        if (code.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_ELEMENT);
        }
    }

}
