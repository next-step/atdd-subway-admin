package nextstep.subway.station;

import nextstep.subway.common.exception.EntityNotFoundException;

public class StationNotFoundException extends EntityNotFoundException {

    public StationNotFoundException(String message) {
        super(message);
    }
}
