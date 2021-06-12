package nextstep.subway.exception.section;

public class NotPossibleRemoveException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotPossibleRemoveException() {
        super();
    }

    public NotPossibleRemoveException(String message) {
        super(message);
    }
}
