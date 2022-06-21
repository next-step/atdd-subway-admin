package nextstep.subway.dto;

public class ApiExceptionResponse {
    private int error;
    private String message;
    private long timestamp;

    public ApiExceptionResponse(int error, String message) {
        this.error = error;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public int getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
