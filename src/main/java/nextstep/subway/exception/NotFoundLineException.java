package nextstep.subway.exception;

public class NotFoundLineException extends IllegalArgumentException {
    private static final String message = "지하철노선(%d)이 없습니다.";

    public NotFoundLineException(Long id) {
        super(String.format(message, id));
    }
}
