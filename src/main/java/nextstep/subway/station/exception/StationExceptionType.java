package nextstep.subway.station.exception;

public enum StationExceptionType {
    NOT_FOUND_STATION("STATION_01", "해당 ID로 지하철역이 존재하지 않습니다.");

    private final String code;
    private final String message;

    StationExceptionType(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
