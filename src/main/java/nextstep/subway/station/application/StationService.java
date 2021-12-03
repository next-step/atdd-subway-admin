package nextstep.subway.station.application;

import nextstep.subway.exception.BadRequestException;
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

    public StationResponse saveStation(StationRequest request) {
        validateDuplicate(request);
        Station persistStation = stationRepository.save(request.toStation());
        return StationResponse.of(persistStation);
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new BadRequestException("존재하지않는 지하철역 ID 입니다."));
    }

    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    private void validateDuplicate(StationRequest request) {
        if (stationRepository.existsByName(request.getName())) {
            throw new BadRequestException("이미 존재하는 역 이름입니다.");
        }
    }
}
