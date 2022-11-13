package nextstep.subway.exception;

public enum StationExceptionMessage {
    NONE_EXISTS_STATION("지하철역이 존재하지 않습니다"),
    EMPTY_STATION_NAME("역이름을 입력해야합니다");
    String message;

    StationExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
