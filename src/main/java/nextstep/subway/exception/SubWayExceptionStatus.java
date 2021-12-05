package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public enum SubWayExceptionStatus {

    DUPLICATE_STATION("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    SubWayExceptionStatus(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
