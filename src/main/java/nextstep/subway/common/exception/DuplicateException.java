package nextstep.subway.common.exception;

public class DuplicateException extends RuntimeException{
    public DuplicateException() {
    }

    public DuplicateException(String message) {
        super(message);
    }
}
