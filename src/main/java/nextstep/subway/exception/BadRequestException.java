package nextstep.subway.exception;

public abstract class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
