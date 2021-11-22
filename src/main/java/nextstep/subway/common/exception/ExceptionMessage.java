package nextstep.subway.common.exception;

public enum ExceptionMessage {
    EXIST_ALL_STATION_TO_SECTION("두개의 역이 모두 노선에 추가되어 있습니다."),
    NON_EXIST_ALL_STATION_TO_SECTION("두 개의 역이 모두 노선에 존재하지 않습니다."),
    GREATER_THAN_OR_EQUAL_DISTANCE("기존 거리보다 크거나 같습니다.");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
