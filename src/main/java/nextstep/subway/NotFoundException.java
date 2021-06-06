package nextstep.subway;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
    }

    public NotFoundException(final String message) {
        super(message);
    }
}
