package nextstep.subway.global.exception;

import org.springframework.http.HttpStatus;

public class CannotDeleteException extends SubwayCustomException {

    public CannotDeleteException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public CannotDeleteException(ExceptionType exceptionType) {
        super(HttpStatus.BAD_REQUEST, exceptionType);
    }
}
