package nextstep.subway.line.application.exceptions;

public class CLineNotFoundException extends RuntimeException{
    public CLineNotFoundException(String message) {
        super(message);
    }
}
