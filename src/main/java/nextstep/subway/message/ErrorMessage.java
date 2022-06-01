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
    LINE_STATION_HAS_STATION_ESSENTIAL("역은 필수입니다."),
    STATION_NAME_IS_ESSENTIAL("역이름은 필수입니다.");


    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String toMessage() {
        return message;
    }
}
