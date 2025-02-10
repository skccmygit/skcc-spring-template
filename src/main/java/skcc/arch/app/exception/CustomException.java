package skcc.arch.app.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    public final ErrorCode errorCode;
    public final Object[] args;

    public CustomException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage(args));
        this.errorCode = errorCode;
        this.args = args;
    }

    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.args = null;
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage(this.args);
    }
}
