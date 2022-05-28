package nextstep.subway.exception;

public class StationNotFoundException extends RuntimeException {
    private static final String MESSAGE_FORMAT = "NOT FOUND, stationId:%s";

    public StationNotFoundException(Long id) {
        super(String.format(MESSAGE_FORMAT, id));
    }
}
