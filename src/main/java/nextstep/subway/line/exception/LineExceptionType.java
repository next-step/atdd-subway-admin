package nextstep.subway.line.exception;

public enum LineExceptionType {
    NOT_FOUND_LINE("LINE_01", "해당 ID로 노선이 존재 하지 않습니다."),
    INVALID_DISTANCE("LINE_02", "거리는 0보다 커야 합니다.");

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
