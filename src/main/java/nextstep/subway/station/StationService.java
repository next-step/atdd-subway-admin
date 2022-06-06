package nextstep.subway.station;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.station.dto.StationRequestDto;
import nextstep.subway.station.dto.StationResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    StationResponseDto saveStation(StationRequestDto stationRequest) {
        Station persistStation = stationRepository.save(stationRequest.toStation());
        return StationResponseDto.of(persistStation);
    }

    List<StationResponseDto> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream().
                map(StationResponseDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }
}
