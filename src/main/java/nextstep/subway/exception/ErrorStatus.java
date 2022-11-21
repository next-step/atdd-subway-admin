package nextstep.subway.exception;

public enum ErrorStatus {
    BAD_REQUEST_NAME("[name] is not null or empty"),
    BAD_REQUEST_COLOR("[color] is not null or empty"),
    BAD_REQUEST_DISTANCE("[distance] must over than 0"),
    BAD_REQUEST_STATION_ID("[station id] must over than 1"),
    DUPLICATE_SECTION("[section] upStation and downStation do not duplicate"),
    DISTANCE_LENGTH("[distance] new Section distance must small exist section distance"),
    SECTION_STATION_ERROR("[section station] 상행 및 하행 역은 반드시 기존에 있는 노선에 하나라도 포함되어야 한다.");
    private String message;

    ErrorStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
