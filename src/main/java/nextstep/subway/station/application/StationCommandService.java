package nextstep.subway.station.application;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StationCommandService implements StationCommandUseCase{
    private final StationRepository stationRepository;
    private final StationQueryUseCase stationQueryUseCase;

    public StationCommandService(StationRepository stationRepository, StationQueryUseCase stationQueryUseCase) {
        this.stationRepository = stationRepository;
        this.stationQueryUseCase = stationQueryUseCase;
    }

    @Override
    public StationResponse saveStation(StationRequest stationRequest) {
        Station persistStation = stationRepository.save(stationRequest.toStation());
        return StationResponse.of(persistStation);
    }

    @Override
    public void deleteStationById(Long id) {
        Station station = stationQueryUseCase.findById(id);
        stationRepository.delete(station);
    }
}
