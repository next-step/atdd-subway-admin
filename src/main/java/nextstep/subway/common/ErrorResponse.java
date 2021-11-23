package nextstep.subway.common;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private ErrorCode errorCode;
    private String message;

    public ErrorResponse() {
        this.errorCode = ErrorCode.SERVER_ERROR;
        this.message = "데이터 저장중 오류가 발생하였습니다.";
    }

    public ErrorResponse(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
