package nextstep.subway.global.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends SubwayCustomException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public NotFoundException(ExceptionType exceptionType) {
        super(HttpStatus.NOT_FOUND, exceptionType);
    }
}
