package nextstep.subway.common;

public enum ErrorCode {

    // 노선 [01xx]
    NOT_FOUND_LINE_ID("0101", "존재하지 않는 노선ID 입니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
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
