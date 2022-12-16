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
@Transactional(readOnly = true)
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

    @Transactional
    public StationResponse saveStation(StationRequest stationRequest) {
        Station persistStation = stationRepository.save(stationMapper.mapToDomainEntity(stationRequest));
        return stationMapper.mapToResponse(persistStation);
    }

    public List<StationResponse> findAllStations() {
        return stationRepository.findAll().stream()
            .map(stationMapper::mapToResponse)
            .collect(Collectors.toList());
    }

    public StationResponse findStationById(Long id) {
        return stationMapper.mapToResponse(findStationByIdAsDomainEntity(id));
    }

    Station findStationByIdAsDomainEntity(Long id) {
        return stationRepository.findById(id)
            .orElseThrow(()-> new NotFoundException(id));
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }
}
