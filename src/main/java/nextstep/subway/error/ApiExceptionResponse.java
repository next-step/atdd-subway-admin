package nextstep.subway.error;

public class ApiExceptionResponse {
    private int errorCode;
    private String error;
    private String message;
    private long timestamp;

    public ApiExceptionResponse(SubwayError error, String message) {
        this.errorCode = error.getCode();
        this.error = error.name();
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
