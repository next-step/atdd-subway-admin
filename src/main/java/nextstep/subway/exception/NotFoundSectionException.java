package nextstep.subway.exception;

public class NotFoundSectionException extends RuntimeException{

    private static final String NOT_FOUND_LINE = "";

    public NotFoundSectionException(String message) {
        super(message);
    }
}
