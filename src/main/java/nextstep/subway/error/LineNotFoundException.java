package nextstep.subway.error;

public class LineNotFoundException extends RuntimeException{

    private static final String MESSAGE_FORMAT = "line not found. lineId:%s";

    public LineNotFoundException(Long id) {
        super(String.format(MESSAGE_FORMAT, id));
    }
}
