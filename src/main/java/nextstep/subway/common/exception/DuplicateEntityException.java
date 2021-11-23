package nextstep.subway.common.exception;

public class DuplicateEntityException extends RuntimeException {
    private static final long serialVersionUID = 2L;

    public DuplicateEntityException(String message) {
        super(message);
    }
}
