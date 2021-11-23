package nextstep.subway.station.exception;

import javax.persistence.EntityNotFoundException;

public class StationNotFoundException extends EntityNotFoundException {

    public StationNotFoundException(String message) {
        super(message);
    }

    public StationNotFoundException() {
        super("지하철역이 존재하지 않습니다.");
    }
}
