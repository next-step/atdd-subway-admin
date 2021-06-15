package nextstep.subway.common;

public enum ErrorCode {
    NOT_FOUND_LINE_MESSAGE("0001", "해당 id의 노선이 존재하지 않습니다."),
    NOT_UNDER_SECTION_DISTANCE_MESSAGE("0002", "기존 역 사이 길이보다 작아야 합니다."),
    EXIST_SAME_STATIONS_MESSAGE("0003", "같은 상행역과 하행역이 이미 모두 노선에 등록 되어있습니다."),
    NOT_EXIST_ANY_SAME_STATION_MESSAGE("0004", "상행역과 하행역 모두 노선에 등록되어 있지 않습니다."),
    NOT_FOUND_STATION_MESSAGE("0005", "해당 id의 역이 존재하지 않습니다."),
    CANNOT_REMOVE_SECTION_SIZE_MESSAGE("0006", "노선에 구간이 한 개인 경우 역을 제거할 수 없습니다.");

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
