package nextstep.subway.station.application;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.exception.NotFoundStationByIdException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();
        return convertStationsResponse(stations);
    }

    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    public List<StationResponse> convertStationsResponse(List<Station> stations) {
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Station getStation(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(NotFoundStationByIdException::new);
    }
}
