package nextstep.subway.common.dto;

public class ErrorResponse {
    private int status;
    private String message;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ErrorResponse of(int value, String message) {
        return new ErrorResponse(value, message);
    }

    public int getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }
}
