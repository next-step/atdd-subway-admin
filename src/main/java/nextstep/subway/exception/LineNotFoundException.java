package nextstep.subway.exception;

public class LineNotFoundException extends RuntimeException {
    private static final String MESSAGE_FORMAT = "NOT FOUND, lineId:%s";

    public LineNotFoundException(Long id) {
        super(String.format(MESSAGE_FORMAT, id));
    }
}
