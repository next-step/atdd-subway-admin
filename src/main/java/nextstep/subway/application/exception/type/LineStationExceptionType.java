package nextstep.subway.application.exception.type;

public enum LineStationExceptionType {
    NOT_FOUND_LINE_STATION_BOTH("상행선, 하행선 둘다 존재하지 않습니다.");

    private final String message;

    LineStationExceptionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
