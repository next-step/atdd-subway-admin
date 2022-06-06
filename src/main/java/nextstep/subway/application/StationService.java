package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.exception.InvalidStationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StationService {

    private static final String INVALID_STATION = "%d : 유효하지 않은 지하철역입니다.";
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

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    public Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(() -> new InvalidStationException(String.format(INVALID_STATION, stationId)));
    }
}
