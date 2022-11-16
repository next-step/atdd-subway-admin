package nextstep.subway.common;

public enum SectionsErrorCode {

    SECTION_DUPLICATION_ERROR("상행선과 하행선이 모두 존재합니다."),
    SECTION_NOT_EXIST_ERROR("상행선과 하행선이 모두 존재하지 않습니다."),
    SECTION_ONE_ERROR("노선의 구간이 하나면 제거할 수 없습니다."),
    SECTION_NOT_EXIST_STATION_ERROR("노선에 등록되지 않은 역은 삭제할 수 없습니다.");

    private final String message;

    SectionsErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
