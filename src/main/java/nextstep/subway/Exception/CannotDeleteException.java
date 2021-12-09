package nextstep.subway.Exception;

public class CannotDeleteException extends RuntimeException{
    public CannotDeleteException(String message) {
        super(message);
    }
}
