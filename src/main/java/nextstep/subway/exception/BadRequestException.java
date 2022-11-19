package nextstep.subway.exception;

public class BadRequestException extends RuntimeException {

    protected BadRequestException(String message) {
        super(message);
    }
}
