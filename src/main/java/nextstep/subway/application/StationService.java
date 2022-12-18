package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StationService {
    private final StationRepository stationRepository;
    private final StationMapper stationMapper;

    public StationService(
        StationRepository stationRepository,
        StationMapper stationMapper
    ) {
        this.stationRepository = stationRepository;
        this.stationMapper = stationMapper;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        Station persistStation = stationRepository.save(stationMapper.mapToDomainEntity(stationRequest));
        return stationMapper.mapToResponse(persistStation);
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStations() {
        return stationRepository.findAll().stream()
            .map(stationMapper::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    Station findStationByIdAsDomainEntity(Long id) {
        return stationRepository.findById(id)
            .orElseThrow(()-> new NotFoundException(id));
    }

    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }
}
