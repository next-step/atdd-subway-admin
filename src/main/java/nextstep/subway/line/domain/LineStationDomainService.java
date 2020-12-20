package nextstep.subway.line.domain;

import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Component;

@Component
public class LineStationDomainService implements SafeStation {
    private StationService stationService;

    public LineStationDomainService(final StationService stationService) {
        this.stationService = stationService;
    }

    @Override
    public SafeStationInfo getStationSafely(final Long stationId) {
        Station station = stationService.getStation(stationId);

        return SafeStationInfo.of(station);
    }
}
