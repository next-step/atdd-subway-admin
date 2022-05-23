package nextstep.subway.error;

public class StationNotFoundException extends RuntimeException {

    private static final String MESSAGE_FORMAT = "station not found. stationId:%s";

    public StationNotFoundException(Long stationId) {
        super(String.format(MESSAGE_FORMAT, stationId));
    }
}
