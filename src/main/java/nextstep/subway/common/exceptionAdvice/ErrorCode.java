package nextstep.subway.common.exceptionAdvice;

public enum ErrorCode {
    LINE_NOT_FOUND_EXCEPTION(6000, "Line Not Found : %d"),
    STATION_NOT_FOUND_EXCEPTION(6001, "Station Not Found : %d"),
    REMOVE_SECTION_EXCEPTION(6500, "최소 1개의 구간은 필수 구간입니다.");

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
