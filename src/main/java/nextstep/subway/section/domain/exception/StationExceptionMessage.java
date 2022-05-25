package nextstep.subway.section.domain.exception;

public enum StationExceptionMessage {
    UP_STATION_IS_NOT_NULL("상행역이 할당되어 있지 않습니다."),
    DOWN_STATION_IS_NOT_NULL("하행역이 할당되어 있지 않습니다."),
    DISTANCE_IS_NOT_NULL("구간이 공란일 수 없습니다."),
    CANNOT_EQUALS_UP_STATION_WITH_DOWN_STATION("상행역과 하행역은 같을 수 없습니다."),
    DISTANCE_IS_MUST_BE_GREATER_THAN_1("거리는 1보다 큰 정수로 구성되어야 한다.");

    String message;

    StationExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
