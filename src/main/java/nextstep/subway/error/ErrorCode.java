package nextstep.subway.error;

/**
 * @author : leesangbae
 * @project : subway
 * @since : 2020-12-28
 */
public enum ErrorCode {

    INPUT_VALUE_INVALID(400, "COM_001", "입력값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(500, "COM_002", "알 수 없는 에러가 발생되었습니다."),

    LINE_NOT_FOUND_EXCEPTION(404, "LINE_001", "해당 노선을 찾을 수 없습니다."),
    ALREADY_SAVED_LINE_EXCEPTION(404, "LINE_002", "이미 등록된 노선입니다."),

    STATION_NOT_FOUND_EXCEPTION(404, "STATION_001", "해당 역을 찾을 수 없습니다.");

    private final int status;
    private final String errorCode;
    private final String errorMessages;

    ErrorCode(int status, String errorCode, String errorMessages) {
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessages = errorMessages;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessages() {
        return errorMessages;
    }
}
