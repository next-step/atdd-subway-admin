package nextstep.subway.common.exception;

public class NotFoundException extends RuntimeException {
    private static final String EXCEPTION_TEXT_NOT_FOUND_DATA = "could not find data";

    public NotFoundException() {
        this(EXCEPTION_TEXT_NOT_FOUND_DATA);
    }

    public NotFoundException(final String message) {
        super(message);
    }
}
