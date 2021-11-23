package nextstep.subway.station.application;

import java.util.Optional;
import nextstep.subway.common.exception.DuplicateException;
import nextstep.subway.common.exception.NotFoundException;
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

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        Station station = stationRequest.toStation();
        validateDuplicateStation(station);

        Station persistStation = stationRepository.save(station);
        return StationResponse.of(persistStation);
    }

    public void deleteStationById(Long id) {
        Station station = findStation(id);
        station.delete();

        stationRepository.save(station);
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
            .map(StationResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Station findStation(Long id) {
        return stationRepository.findById(id)
            .orElseThrow(NotFoundException::new);
    }

    @Transactional(readOnly = true)
    protected void validateDuplicateStation(Station station) {
        Optional<Station> optionalStation = stationRepository.findByName(station.getName());

        optionalStation.ifPresent(findLine -> {
            throw new DuplicateException("이미 등록된 역 이름을 사용할 수 없습니다.");
        });
    }
}
