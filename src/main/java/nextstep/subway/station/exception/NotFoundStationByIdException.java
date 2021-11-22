package nextstep.subway.station.exception;

public class NotFoundStationByIdException extends IllegalArgumentException {
    public NotFoundStationByIdException(String message) {
        super(message);
    }
}
