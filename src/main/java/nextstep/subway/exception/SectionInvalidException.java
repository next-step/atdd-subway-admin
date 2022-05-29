package nextstep.subway.exception;

public class SectionInvalidException extends RuntimeException {

    public SectionInvalidException() {
    }

    public SectionInvalidException(String message) {
        super(message);
    }
}
