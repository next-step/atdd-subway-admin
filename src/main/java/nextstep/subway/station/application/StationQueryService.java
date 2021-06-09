package nextstep.subway.station.application;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StationQueryService implements StationQueryUseCase {
    public static final String STATION_ID_NOT_FOUND_EXCEPTION_MESSAGE = "해당 ID로 된 지하철 역이 존재하지 않습니다.";

    private final StationRepository stationRepository;

    public StationQueryService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Override
    public List<StationResponse> findAllStations() {
        return stationRepository.findAll()
                .stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Override
    public Station findById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new StationNotFoundException(STATION_ID_NOT_FOUND_EXCEPTION_MESSAGE));
    }
}
