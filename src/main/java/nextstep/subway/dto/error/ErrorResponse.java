package nextstep.subway.dto.error;

public class ErrorResponse {

    private String message;
    private Integer statusCode;

    public ErrorResponse() {
    }

    private ErrorResponse(String message, Integer statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public static ErrorResponse of(String message, Integer statusCode) {
        return new ErrorResponse(message, statusCode);
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
