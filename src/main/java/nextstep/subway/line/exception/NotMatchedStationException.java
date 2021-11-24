package nextstep.subway.line.exception;

import nextstep.subway.common.exception.ServiceException;
import nextstep.subway.station.domain.Station;

public class NotMatchedStationException extends ServiceException {
    private static final String DEFAULT_MESSAGE = "두 역이 아무런 구간과 연결되어 있지 않습니다. 상행역 : %s, 하행역 : %s";
    public NotMatchedStationException(Station upStation, Station downStation) {
        super(String.format(DEFAULT_MESSAGE, upStation.getName(), downStation.getName()));
    }
}
