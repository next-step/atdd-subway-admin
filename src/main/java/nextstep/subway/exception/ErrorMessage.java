package nextstep.subway.exception;

public enum ErrorMessage {

    LINE_ID_NOT_FOUND("노선 아이디 값을 찾을 수 없습니다. lineId: %s"),
    STATION_ID_NOT_FOUND("지하철 아이디 값을 찾을 수 없습니다."),
    LINE_NAME_NOT_DUPLICATED("노선의 이름은 중복될 수 없습니다."),
    INTERNAL_SERVER_ERROR("알 수 없는 오류입니다."),
    SAME_SUBWAY_SECTION_ERROR("상행선과 하행선이 동일할 수 없습니다."),
    DISTANCE_CANNOT_BE_ZERO("거리는 0이하가 될 수 없습니다."),
    ;
    private final String message;


    ErrorMessage(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}
