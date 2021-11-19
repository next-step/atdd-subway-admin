package nextstep.subway.common;

public class ExceptionMessage {
    private final String message;

    private ExceptionMessage(String message) {
        this.message = message;
    }

    public static ExceptionMessage of(Exception message) {
        return new ExceptionMessage(message.getMessage());
    }

    public String getMessage() {
        return message;
    }
}
