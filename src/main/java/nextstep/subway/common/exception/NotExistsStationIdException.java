package nextstep.subway.common.exception;

public class NotExistsStationIdException extends NotExistsIdException {
    public NotExistsStationIdException(Long id) {
        super("station_id " + id + " is not exists");
    }
}
