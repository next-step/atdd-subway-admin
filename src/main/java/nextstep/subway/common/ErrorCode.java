package nextstep.subway.common;

public enum ErrorCode {
    NOT_FOUND_LINE_MESSAGE("0001", "해당 id의 노선이 존재하지 않습니다."),
    NOT_UNDER_SECTION_DISTANCE_MESSAGE("0002", "기존 역 사이 길이보다 작아야 합니다.");

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
