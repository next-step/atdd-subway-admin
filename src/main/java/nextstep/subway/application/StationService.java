package nextstep.subway.application;

import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.dto.StationResponses;
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
        Station persistStation = stationRepository.save(stationRequest.toStation());
        return StationResponse.of(persistStation);
    }

    @Transactional(readOnly = true)
    public StationResponses findAllStations() {
        List<Station> stations = stationRepository.findAll();

        List<StationResponse> list = stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return new StationResponses(list);
    }

    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }
}
