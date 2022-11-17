package nextstep.subway.exception;

public class IllegalRequestBody extends RuntimeException{
    public IllegalRequestBody(String message) {
        super(message);
    }
}
