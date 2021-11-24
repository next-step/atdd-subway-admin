package nextstep.subway.line.exception;

import nextstep.subway.common.exception.ServiceException;
import nextstep.subway.station.domain.Station;

public class DuplicateBothStationException extends ServiceException {
    private static final String DEFAULT_MESSAGE = "두 역이 이미 구간으로 연결되어 있습니다. 상행역 : %s, 하행역 : $s";
    public DuplicateBothStationException(Station upStation, Station downStation) {
        super(String.format(DEFAULT_MESSAGE, upStation.getName(), downStation.getName()));
    }
}
