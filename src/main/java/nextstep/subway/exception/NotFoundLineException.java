package nextstep.subway.exception;

public class NotFoundLineException extends RuntimeException {
    public NotFoundLineException(Long id) {
        super(String.format("[Line id : %d] not found", id));
    }
}
