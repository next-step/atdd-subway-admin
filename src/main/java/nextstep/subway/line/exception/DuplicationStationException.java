package nextstep.subway.line.exception;

import nextstep.subway.common.exception.DuplicationException;

public class DuplicationStationException extends DuplicationException {
    public static final String STATION_DUPLICATION = "상행선과 하행선은 동일할 수 없습니다.";

    public DuplicationStationException() {
        super(STATION_DUPLICATION);
    }
}
