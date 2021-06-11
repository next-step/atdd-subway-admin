package nextstep.subway.common;

public class ErrorMessage {

    private final String message;

    public ErrorMessage(String message) {
        this.message = message;
    }

    public static ErrorMessage of(String message) {
        return new ErrorMessage(message);
    }

    public String getMessage() {
        return message;
    }
}
