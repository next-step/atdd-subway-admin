package nextstep.subway.station.exception;

import nextstep.subway.core.exception.NotFoundException;

public class NotFoundStationException extends NotFoundException {

    public NotFoundStationException(Long id) {
        super(String.format("해당하는 지하철역을 찾을 수 없습니다. (id: %d)", id));
    }
}
