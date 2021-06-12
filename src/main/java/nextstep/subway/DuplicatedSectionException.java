package nextstep.subway;

public class DuplicatedSectionException extends RuntimeException {

    public DuplicatedSectionException() {
    }

    public DuplicatedSectionException(final String message) {
        super(message);
    }
}
