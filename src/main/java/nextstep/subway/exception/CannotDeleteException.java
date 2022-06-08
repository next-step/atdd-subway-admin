package nextstep.subway.exception;

public class CannotDeleteException extends RuntimeException {
    public CannotDeleteException() {
        super();
    }

    public CannotDeleteException(String s) {
        super(s);
    }
}
