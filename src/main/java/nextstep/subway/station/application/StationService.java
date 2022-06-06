package nextstep.subway.station.application;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StationService {
    public static final String UP_STATION_KEY = "upStation";
    public static final String DOWN_STATION_KEY = "downStation";

    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationResponse saveStation(StationRequest stationRequest) {
        Station persistStation = stationRepository.save(stationRequest.toStation());
        return StationResponse.of(persistStation);
    }

    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(station -> StationResponse.of(station))
                .collect(Collectors.toList());
    }

    public Station findById(Long id) {
        Station station = stationRepository.findById(id)
                                            .orElseThrow(EntityNotFoundException::new);
        return station;
    }

    public Map<String, Station> findUpDownStation(Long upStationId, Long downStationId) {
        Map<String, Station> result = new HashMap<>();
        List<Station> stations = stationRepository.findAll();

        stations.stream()
                .forEach(station -> {
                    if (upStationId.equals(station.getId())) {
                        result.put(UP_STATION_KEY, station);
                    }
                    if (downStationId.equals(station.getId())) {
                        result.put(DOWN_STATION_KEY, station);
                    }
                });

        return result;
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }
}
