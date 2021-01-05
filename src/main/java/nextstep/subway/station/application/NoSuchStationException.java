package nextstep.subway.station.application;

public class NoSuchStationException extends RuntimeException {
    public NoSuchStationException(Long id) {
        super(id.toString());
    }
}
