package nextstep.subway.common.exception;

public class NotFoundEntityException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotFoundEntityException(String message) {
        super(message);
    }
}
