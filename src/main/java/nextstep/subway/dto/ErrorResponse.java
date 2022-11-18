package nextstep.subway.dto;

public class ErrorResponse {

    private final String status;
    private final int code;
    private final String message;

    public ErrorResponse(String status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
