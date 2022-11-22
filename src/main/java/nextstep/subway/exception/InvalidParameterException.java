package nextstep.subway.exception;

public class InvalidParameterException extends RuntimeException {

    private String message;

    public InvalidParameterException(String message) {
        super(message);
    }
}
