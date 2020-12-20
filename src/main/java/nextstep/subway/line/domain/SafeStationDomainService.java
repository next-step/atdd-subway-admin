package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exceptions.StationNotFoundException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Component;

@Component
public class SafeStationDomainService implements SafeStation {
    private StationService stationService;

    public SafeStationDomainService(final StationService stationService) {
        this.stationService = stationService;
    }

    @Override
    public SafeStationInfo getStationSafely(final Long stationId) {
        try {
            Station station = stationService.getStation(stationId);

            return SafeStationInfo.of(station);
        } catch (Exception e) {
            throw new StationNotFoundException("해당 역을 찾지 못했습니다.");
        }
    }
}
