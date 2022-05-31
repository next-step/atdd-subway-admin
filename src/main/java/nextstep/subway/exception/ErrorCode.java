package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    LINE_NOT_FOUND(HttpStatus.NOT_FOUND, "노선을 찾을 수 없습니다."),
    STATION_NOT_FOUND(HttpStatus.NOT_FOUND, "역을 찾을 수 없습니다."),
    NON_VALID_LINE_STATION(HttpStatus.BAD_REQUEST, "구간 등록 요청이 유효하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
