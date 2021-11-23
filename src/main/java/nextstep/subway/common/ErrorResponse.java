package nextstep.subway.common;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private HttpStatus httpStatus;
    private String message;

    public ErrorResponse() {
        httpStatus = HttpStatus.BAD_REQUEST;
        message = "데이터 저장중 오류가 발생하였습니다.";
    }

    public ErrorResponse(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
