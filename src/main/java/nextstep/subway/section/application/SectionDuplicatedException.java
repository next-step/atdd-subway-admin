package nextstep.subway.section.application;

public class SectionDuplicatedException extends RuntimeException {

    public SectionDuplicatedException() {
        super();
    }

    public SectionDuplicatedException(String message) {
        super(message);
    }

    public SectionDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SectionDuplicatedException(Throwable cause) {
        super(cause);
    }
}
