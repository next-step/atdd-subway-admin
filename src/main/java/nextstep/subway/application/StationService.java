package nextstep.subway.application;

import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationResponse saveStation(StationRequest stationRequest) {
        Station station = stationRepository.save(stationRequest.toStation());
        return StationResponse.of(station);
    }

    @Transactional
    public StationResponse upsert(StationRequest stationRequest) {
        return stationRepository.findByName(stationRequest.getName())
                .map(StationResponse::of)
                .orElseGet(() -> StationResponse.of(stationRepository.save(stationRequest.toStation())));
    }

    public List<StationResponse> findAllStations() {
        return stationRepository.findAll()
                .stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStationById(Long id) {
        Station station = stationRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        stationRepository.deleteById(station.getId());
    }
}
