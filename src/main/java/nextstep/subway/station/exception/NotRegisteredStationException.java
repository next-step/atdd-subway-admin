package nextstep.subway.station.exception;

/**
 * @author : leesangbae
 * @project : subway
 * @since : 2021-01-04
 */
public class NotRegisteredStationException extends RuntimeException {

    public NotRegisteredStationException() {
    }

    public NotRegisteredStationException(String message) {
        super(message);
    }
}
