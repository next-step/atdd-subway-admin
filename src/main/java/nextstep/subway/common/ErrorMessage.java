package nextstep.subway.common;

public enum ErrorMessage {
    NOT_FOUND("해당 id로 조회 결과가 없습니다."),
    NOT_ALLOW_ADD_SECTION("상행역과 하행역 둘 중 하나라도 포함되어있지 않으면 구간을 추가할 수 없습니다."),
    DUPLICATE_SECTION("동일한 구간이 존재합니다."),
    LINE_SECTION_NOT_NULL("해당역을 포함하는 노선이 존재하지 않습니다."),
    CANNOT_REMOVE_STATION_NOT_INCLUDE_LINE("노선에 포함되어 있지 않은 역을 제거할 수 없습니다."),
    CANNOT_REMOVE_STATION_ONLY_ONE_SECTION("노선에 구간이 1개이면 삭제할 수 없습니다."),
    NOT_FOUND_SECTION("삭제할 구간을 찾을 수 없습니다."),
    ;

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
