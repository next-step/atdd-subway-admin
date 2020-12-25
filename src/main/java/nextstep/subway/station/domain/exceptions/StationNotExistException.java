package nextstep.subway.station.domain.exceptions;

public class StationNotExistException extends RuntimeException {
    public StationNotExistException(String message) {
        super(message);
    }
}
