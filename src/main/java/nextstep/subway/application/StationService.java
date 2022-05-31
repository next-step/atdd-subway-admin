package nextstep.subway.application;

import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.dto.station.StationRequestDTO;
import nextstep.subway.dto.station.StationResponseDTO;
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
    public StationResponseDTO saveStation(StationRequestDTO stationRequestDTO) {
        Station persistStation = stationRepository.save(stationRequestDTO.toStation());
        return StationResponseDTO.of(persistStation);
    }

    public List<StationResponseDTO> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(station -> StationResponseDTO.of(station))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    @Transactional
    public Station findStation(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] ID에 해당하는 지하철역이 없습니다."));
    }

}
