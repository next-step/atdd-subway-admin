package nextstep.subway.exception;

public enum SubwayExceptionMessage {
    DUPLICATE_SECTION("이미 등록되어 있는 구간입니다."),
    INVALID_DISTANCE("역과 역사이에 등록시에 거리는 기존보다 크거나 같을 수 없습니다."),
    ;

    private final String message;

    SubwayExceptionMessage(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
