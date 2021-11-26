package nextstep.subway.exception;

public class LineNameAlreadyExistsException extends SubwayRuntimeException {
    public static final String MESSAGE = "이미 같은 이름의 지하철역이 있습니다.";

    public LineNameAlreadyExistsException() {
        super(MESSAGE);
    }

    public LineNameAlreadyExistsException(final Throwable cause) {
        super(MESSAGE, cause);
    }
}
