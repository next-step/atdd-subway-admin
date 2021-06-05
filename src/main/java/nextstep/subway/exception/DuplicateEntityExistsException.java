package nextstep.subway.exception;

public class DuplicateEntityExistsException extends RuntimeException {

    public DuplicateEntityExistsException(String message) {
        super(message);
    }
}
