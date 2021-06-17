package nextstep.subway.station.application;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.dto.StationsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        Station persistStation = stationRepository.save(stationRequest.toStation());
        return StationResponse.of(persistStation);
    }

    @Transactional(readOnly = true)
    public StationsResponse findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return StationsResponse.of(stations);
    }

    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    public Station findById(long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(IllegalArgumentException::new);
    }
}
