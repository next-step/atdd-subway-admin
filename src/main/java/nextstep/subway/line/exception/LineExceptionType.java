package nextstep.subway.line.exception;

public enum LineExceptionType {
    NOT_FOUND_LINE("LINE_01", "해당 ID로 노선이 존재 하지 않습니다."),
    INVALID_DISTANCE("LINE_02", "거리는 0보다 커야 합니다."),
    FINAL_UP_STATION_NOT_FOUND("LINE_03", "상행 종착역을 찾을수 없습니다."),
    FINAL_DOWN_STATION_NOT_FOUND("LINE_04", "하행 종착역을 찾을수 없습니다."),
    INVALID_UPDATE_DISTANCE("LINE_05", "기존 구간과 거리가 일치 합니다."),
    STATION_ALL_USED("LINE_06", "상행, 하행이 이미 존재 합니다"),
    STATION_ALL_NOT_USED("LINE_07", "상행, 하행이 모두 등록 되지 않았습니다.");

    private final String code;
    private final String message;

    LineExceptionType(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
