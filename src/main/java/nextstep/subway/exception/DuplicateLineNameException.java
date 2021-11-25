package nextstep.subway.line.application.exception;

public class DuplicateLineNameException extends RuntimeException{

    public DuplicateLineNameException(String message) {
        super(message);
    }
}
