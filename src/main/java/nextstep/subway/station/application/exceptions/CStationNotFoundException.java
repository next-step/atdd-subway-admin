package nextstep.subway.station.application.exceptions;

public class CStationNotFoundException extends RuntimeException{
    public CStationNotFoundException(String message) {
        super(message);
    }
}
