package nextstep.subway.common;

public class ErrorResponse {

    private final String code;
    private final String message;

    private ErrorResponse(ErrorCode code) {
        this.code = code.getCode();
        this.message = code.getMessage();
    }

    public static ErrorResponse of(ErrorCode code) {
        return new ErrorResponse(code);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
