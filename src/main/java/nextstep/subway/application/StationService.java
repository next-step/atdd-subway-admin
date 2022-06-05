package nextstep.subway.application;

import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static nextstep.subway.common.Messages.NOT_FOUND_STATION_ERROR;

@Service
@Transactional(readOnly = true)
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationResponse saveStation(StationRequest stationRequest) {
        Station persistStation = stationRepository.save(stationRequest.toStation());
        return StationResponse.of(persistStation);
    }

    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStationById(Long id) {
        Station station = findStationById(id);
        station.delete();
    }

    private Station findStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(NOT_FOUND_STATION_ERROR));
    }

    public Station findStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new NoSuchElementException(NOT_FOUND_STATION_ERROR));
    }
}
