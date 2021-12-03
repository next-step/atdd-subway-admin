package nextstep.subway.line.exception;

import static nextstep.subway.common.Message.*;

import nextstep.subway.common.exception.DuplicationException;

public class DuplicateSectionStationException extends DuplicationException {


    public DuplicateSectionStationException() {
        super(STATION_DUPLICATION.getMessage());
    }
}
