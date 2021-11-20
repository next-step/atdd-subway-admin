package nextstep.subway.common.ui;

public class ErrorResponse {
    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(Exception error) {
        this(error.getMessage());
    }

    public String getMessage() {
        return message;
    }
}
