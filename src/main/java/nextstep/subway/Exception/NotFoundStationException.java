package nextstep.subway.Exception;

import javassist.NotFoundException;

public class NotFoundStationException extends NotFoundException {
    private static final String MESSAGE = "해당하는 지하철역을 찾을 수 없습니다. (id: %d)";

    public NotFoundStationException(Long stationId) {
        super(String.format(MESSAGE, stationId));
    }
}
