package nextstep.subway.station.application;

import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStations() {
        return StationResponse.ofList(stationRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Station findByStationId(Long id) throws NotFoundException {
        return stationRepository.findById(id).orElseThrow(() -> new NotFoundException("데이터가 존재하지 않습니다."));
    }

    public StationResponse saveStation(StationRequest request) {
        final Station savedStation = stationRepository.save(request.toStation());
        return StationResponse.of(savedStation);
    }

    public void deleteStationById(Long id) {
        final Station station = findByStationId(id);
        stationRepository.deleteById(station.getId());
    }
}
