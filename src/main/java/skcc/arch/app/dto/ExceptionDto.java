package skcc.arch.app.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import skcc.arch.app.exception.CustomException;

@Getter
@Builder
@RequiredArgsConstructor
public class ExceptionDto {

    private final Integer code;
    private final String message;

    public ExceptionDto(CustomException e) {
        this.code = e.getErrorCode().getCode();
        this.message = e.getErrorCode().getMessage();
    }

    public ExceptionDto(Exception e) {
        this.code = e.getClass().getSimpleName().hashCode();
        this.message = e.getMessage();
    }
}
