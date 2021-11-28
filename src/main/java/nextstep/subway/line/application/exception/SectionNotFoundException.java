package nextstep.subway.line.application.exception;

public class SectionNotFoundException extends RuntimeException {
    public SectionNotFoundException() {
        super();
    }

    public SectionNotFoundException(String message) {
        super(message);
    }

    public static SectionNotFoundException error(String message) {
        return new SectionNotFoundException(message);
    }
}
