package nextstep.subway.exception;

public class NotFoundLineException extends RuntimeException{

    private static final String NOT_FOUND_LINE = "";

    public NotFoundLineException(String message) {
        super(message);
    }
}
