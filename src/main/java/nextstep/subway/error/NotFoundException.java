package nextstep.subway.error;

public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 4443916265741861468L;

    public NotFoundException(String message) {
        super(message);
    }
}
