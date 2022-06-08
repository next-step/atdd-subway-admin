package nextstep.subway.station.application;

import nextstep.subway.station.domain.SectionStations;
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

    public SectionStations findUpDownStation(Long upStationId, Long downStationId) {
        List<Station> stations = stationRepository.findAll();
        SectionStations sectionStations = new SectionStations(stations, upStationId, downStationId);
        return sectionStations;
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }
}
