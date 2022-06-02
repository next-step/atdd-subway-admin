package nextstep.subway.message;


public enum ErrorMessage {


    LINE_NONE_EXIST("존재 하지 않은 노선입니다."),
    LINE_NOT_VALID_UP_STATION("유효하지 않은 상행종점입니다."),
    LINE_NOT_VALID_DOWN_STATION("유효하지 않은 하행종점입니다."),
    LINE_DUPLICATE("중복된 노선입니다."),
    LINE_CHANGE_IS_NO_NAME("변경할 이름이 없습니다."),
    LINE_CHANGE_IS_NO_COLOR("변경할 색상명이 없습니다."),
    LINE_NAME_IS_ESSENTIAL("노선의 이름은 필수 입니다."),
    LINE_COLOR_IS_ESSENTIAL("노선의 색상은 필수 입니다."),

    DISTANCE_VALUE_IS_MIN_ONE("거리는 0보다 커야 합니다."),
    STATION_NAME_IS_ESSENTIAL("역이름은 필수입니다."),

    SECTION_HAS_UP_STATION_ESSENTIAL("상행역은 필수입니다."),
    SECTION_HAS_DOWN_STATION_ESSENTIAL("하행역은 필수입니다."),
    SECTION_HAS_DISTANCE_STATION_ESSENTIAL("거리은 필수입니다."),

    SECTION_ADD_DISTANCE_IS_BIG("구간에 추가할 길이가 너무 깁니다."),
    SECTION_UP_STATION_AND_DOWN_STATION_EXIST("상행선과 하행선이 이미 존재하는 구간입니다."),
    SECTION_IS_NO_SEARCH("구간을 찾을수 없습니다.")
    ;




    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String toMessage() {
        return message;
    }
}
