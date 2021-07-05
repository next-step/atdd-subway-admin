package nextstep.subway.exception;

public class CanNotFoundLineException extends RuntimeException{
    public CanNotFoundLineException(String message) {
        super(message);
    }
}
