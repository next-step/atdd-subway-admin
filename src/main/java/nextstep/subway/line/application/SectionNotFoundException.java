package nextstep.subway.line.application;

public class SectionNotFoundException extends RuntimeException {
    public SectionNotFoundException() {
        super();
    }

    public SectionNotFoundException(String message) {
        super(message);
    }

    public SectionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SectionNotFoundException(Throwable cause) {
        super(cause);
    }
}
