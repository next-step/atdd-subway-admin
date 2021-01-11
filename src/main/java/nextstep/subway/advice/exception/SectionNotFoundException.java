package nextstep.subway.advice.exception;

import nextstep.subway.station.domain.Station;

public class SectionNotFoundException extends RuntimeException {

    public SectionNotFoundException(Station station) {
        super(String.format("하나인 구간은 제거할 수 없습니다 (StationId : %d)", station.getId()));
    }
}
