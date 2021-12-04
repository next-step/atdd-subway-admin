package nextstep.subway.common.exception;

public enum SubwayErrorCode {
    INVALID_DISTANCE("1 이상의 길이만 입력 가능합니다."),
    ALREADY_CONTAINS_ALL_STATION("이미 모두 구간에 포함되어 있습니다."),
    NOT_CONTAINS_ANY_STATION("모두 구간에 포함되어 있지 않습니다."),
    STATION_NOT_EXISTS("존재하지 않는 지하철 역 입니다."),
    CANNOT_DELETE_LAST_LINE("구간이 하나인 노선에서 마지막 구간을 제거할 수 없습니다.");

    private final String message;

    SubwayErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
