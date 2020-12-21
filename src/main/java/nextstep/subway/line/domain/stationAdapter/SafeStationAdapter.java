package nextstep.subway.line.domain.stationAdapter;

import nextstep.subway.line.domain.exceptions.StationNotFoundException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SafeStationAdapter implements SafeStation {
    private StationService stationService;

    public SafeStationAdapter(final StationService stationService) {
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

    public List<SafeStationInfo> getStationsSafely(final List<Long> ids) {
        List<Station> stations = stationService.getStations(ids);
        System.out.println(stations.get(0));

        return stations.stream()
                .map(SafeStationInfo::of)
                .sorted(Comparator.comparingLong(SafeStationInfo::getId))
                .collect(Collectors.toList());
    }
}
