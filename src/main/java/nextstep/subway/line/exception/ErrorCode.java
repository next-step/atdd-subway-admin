package nextstep.subway.line.exception;

public enum ErrorCode {
    NOT_FOUND_LINE_MESSAGE("0001", "해당 id의 노선이 존재하지 않습니다.");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
