package nextstep.subway.common.exceptionAdvice;

public enum ErrorCode {
    LINE_NOT_FOUND_EXCEPTION(6000, "Line Not Found : %d"),
    STATION_NOT_FOUND_EXCEPTION(6001, "Station Not Found : %d");

    private int errorCode;
    private String errorMessage;

    ErrorCode(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
