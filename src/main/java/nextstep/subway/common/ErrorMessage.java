package nextstep.subway.common;

public enum ErrorMessage {
    NOT_FOUND("해당 id로 조회 결과가 없습니다."),
    LINE_NOT_NULL("노선이 존재하지 않습니다."),
    STATION_NOT_NULL("역이 존재하지 않습니다."),
    SECTION_NOT_NULL("구간이 존재하지 않습니다."),
    NOT_ALLOW_ADD_SECTION("상행역과 하행역 둘 중 하나라도 포함되어있지 않으면 구간을 추가할 수 없습니다."),
    DUPLICATE_SECTION("동일한 구간이 존재합니다."),
    ;

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
