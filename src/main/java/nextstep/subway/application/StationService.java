package nextstep.subway.application;

import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationName;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.dto.station.StationRequest;
import nextstep.subway.dto.station.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationResponse saveStation(StationRequest stationRequest) {
        validateDuplicatedName(stationRequest.getName());

        Station persistStation = stationRepository.save(stationRequest.toStation());
        return StationResponse.of(persistStation);
    }

    private void validateDuplicatedName(String name) {
        Optional<Station> stationByName = stationRepository.findByName(StationName.of(name));

        stationByName.ifPresent(station -> {
            throw new IllegalArgumentException("중복된 지하철역 이름입니다.");
        });
    }

    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }
}
