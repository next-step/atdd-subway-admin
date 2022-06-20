package nextstep.subway.exception;

public enum SubwayExceptionMessage {
    DUPLICATE_SECTION("이미 등록되어 있는 구간입니다."),
    INVALID_DISTANCE("역과 역사이에 등록시에 거리는 기존보다 크거나 같을 수 없습니다."),
    EQUALS_UP_AND_DOWN_STATION("상행선역과 하행선 역을 같을 수 없습니다."),
    DO_NOT_DELETE_LAST_SECTION("마지막 구간의 역은 지울 수 없습니다."),
    NOT_FOUND_STATION("노선에 존재 하지않는 역입니다.")
    ;

    private final String message;

    SubwayExceptionMessage(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
