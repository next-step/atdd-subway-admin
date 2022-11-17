package nextstep.subway.exception;

public enum ErrorMessage {

    LINE_ID_NOT_FOUND("노선 아이디 값을 찾을 수 없습니다. lineId: %s"),
    STATION_ID_NOT_FOUND("지하철 아이디 값을 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR("알 수 없는 오류입니다."),
    SAME_SUBWAY_SECTION_ERROR("상행선과 하행선이 동일할 수 없습니다."),
    DISTANCE_CANNOT_BE_ZERO("거리는 0이하가 될 수 없습니다."),
    DISTANCE_BETWEEN_STATION_OVER("역 사이의 거리가 같거나 큽니다."),
    UP_STATION_AND_DOWN_STATION_ENROLLMENT("상행역과 하행역이 모두 등록되어 있습니다."),
    UP_STATION_AND_DOWN_STATION_NOT_FOUND("상행역과 하행역이 모두 등록되어 있지 않습니다."),
    UP_STATION_NOT_FOUND("해당 노선의 상행역을 찾지 못했습니다."),
    ;
    private final String message;

    ErrorMessage(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}
