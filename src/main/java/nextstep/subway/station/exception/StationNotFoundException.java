package nextstep.subway.station.exception;

import nextstep.subway.common.exception.NotFoundException;

public class StationNotFoundException extends NotFoundException {
    private static final String DEFAULT_MESSAGE = "역을 찾을 수 없습니다 : ";
    public StationNotFoundException(Long id) {
        super(DEFAULT_MESSAGE + id);
    }
}
