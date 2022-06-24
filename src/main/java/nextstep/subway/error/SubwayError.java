package nextstep.subway.error;

public enum SubwayError {
    NOT_DEFINED(0),
    LINE_NOT_FOUND(1004),
    STATION_NOT_FOUND(2004),
    ;

    SubwayError(int code) {
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }
}
