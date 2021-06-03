package nextstep.subway.common.exception;

public class ExceptionResponse {
    private final String message;

    public ExceptionResponse(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
