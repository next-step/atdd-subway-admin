package nextstep.subway.common;

public enum Messages {
    NO_STATION("역정보가 없습니다."),
    NO_LINE("노선정보가 없습니다."),
    NOT_INCLUDE_SECTION("상행역과 하행역이 포함된 구간이 없습니다."),
    ALREADY_EXISTS_SECTION("상행역, 하행역이 이미 구간으로 등록되어 있습니다."),
    LONG_OR_SAME_DISTANCE("구간의 길이는 기존 구간보다 작아야 합니다."),
    SECTION_REQUIRED_STATION("역정보는 필수입니다."),
    NOT_POSITIVE_NUMBER("구간의 길이는 0보다 커야 합니다.");

    private String values;

    Messages(String values) {
        this.values = values;
    }

    public String getValues() {
        return values;
    }
}
