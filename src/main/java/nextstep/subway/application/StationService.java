package nextstep.subway.application;

import javassist.NotFoundException;
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
    public static final String STATION_NOT_FOUND = "역을 찾을 수 없습니다.";
    private final StationRepository stationRepository;

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
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Station findById(Long id) throws NotFoundException {
        return stationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(STATION_NOT_FOUND));
    }


    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }
}
