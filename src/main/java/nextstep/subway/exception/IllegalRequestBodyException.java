package nextstep.subway.exception;

public class IllegalRequestBodyException extends RuntimeException{
    public IllegalRequestBodyException(String message) {
        super(message);
    }
}
