package nextstep.subway.exception;

public class ImpossibleDeleteException extends RuntimeException {
    public ImpossibleDeleteException() {
        super();
    }

    public ImpossibleDeleteException(String message) {
        super(message);
    }
}
