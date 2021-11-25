package nextstep.subway.domain.station.application;

import nextstep.subway.domain.station.domain.Station;
import nextstep.subway.domain.station.domain.StationRepository;
import nextstep.subway.domain.station.dto.StationRequest;
import nextstep.subway.domain.station.dto.StationResponse;
import nextstep.subway.global.error.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Station findStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("ID가 %d인 지하철 역은 존재하지 않습니다", stationId)));
    }
}
