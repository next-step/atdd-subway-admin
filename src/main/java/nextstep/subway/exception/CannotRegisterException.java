package nextstep.subway.exception;

public class CannotRegisterException extends SubwayCustomException {

    public CannotRegisterException(String message) {
        super(message);
    }

    public CannotRegisterException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
