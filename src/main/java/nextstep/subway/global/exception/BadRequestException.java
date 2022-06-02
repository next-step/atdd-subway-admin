package nextstep.subway.global.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends SubwayCustomException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public BadRequestException(ExceptionType exceptionType) {
        super(HttpStatus.BAD_REQUEST, exceptionType);
    }
}
