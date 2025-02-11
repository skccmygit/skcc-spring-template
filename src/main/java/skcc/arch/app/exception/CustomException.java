package skcc.arch.app.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    public final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage(args));
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
