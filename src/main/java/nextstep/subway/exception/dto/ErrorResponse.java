package nextstep.subway.exception.dto;

public class ErrorResponse {

    private String message;

    private ErrorResponse(String message) {
        this.message = message;
    }

    public static ErrorResponse valueOf(String message) {
        return new ErrorResponse(message);
    }

    public String getMessage() {
        return message;
    }
}
