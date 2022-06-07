package nextstep.subway.station.exception;

public class StationException extends RuntimeException {
    private final String code;

    public StationException(final String code, final String message) {
        super(message);
        this.code = code;
    }

    public StationException(final StationExceptionType type) {
        super(type.getMessage());
        this.code = type.getCode();
    }

    public String getCode() {
        return code;
    }
}
