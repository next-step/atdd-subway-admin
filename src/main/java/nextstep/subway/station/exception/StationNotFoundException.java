package nextstep.subway.station.exception;

/**
 * @author : leesangbae
 * @project : subway
 * @since : 2020-12-28
 */
public class StationNotFoundException extends RuntimeException {

    public StationNotFoundException() {
    }

    public StationNotFoundException(String message) {
        super(message);
    }
}
