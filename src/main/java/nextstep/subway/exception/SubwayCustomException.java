package nextstep.subway.exception;

public class SubwayCustomException extends RuntimeException {
    public SubwayCustomException(String message) {
        super(message);
    }

    public SubwayCustomException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
