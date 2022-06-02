package nextstep.subway.global.exception;

import org.springframework.http.HttpStatus;

public class CannotRegisterException extends SubwayCustomException {

    public CannotRegisterException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public CannotRegisterException(ExceptionType exceptionType) {
        super(HttpStatus.BAD_REQUEST, exceptionType);
    }
}
