package nextstep.subway.station.application;

import nextstep.subway.common.NotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StationService {
    public static final String THE_REQUESTED_INFORMATION_DOES_NOT_EXIST = "The requested information does not exist.";
    private StationRepository stationRepository;
    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        Station persistStation = stationRepository.save(stationRequest.toStation());
        return StationResponse.from(persistStation);
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(station -> StationResponse.from(station))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StationResponse findByStationId(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(THE_REQUESTED_INFORMATION_DOES_NOT_EXIST));
        return StationResponse.from(station);
    }

    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }
}
