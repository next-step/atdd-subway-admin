package nextstep.subway.station.application;

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
@Transactional(readOnly = true)
public class StationService {
    public static final String THE_REQUESTED_INFORMATION_DOES_NOT_EXIST = "The requested information does not exist.";
    private final StationRepository stationRepository;
    public StationService(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationResponse saveStation(final StationRequest stationRequest) {
        final Station persistStation = stationRepository.save(stationRequest.toStation());
        return StationResponse.from(persistStation);
    }

    public List<StationResponse> findAllStations() {
        final List<Station> stations = stationRepository.findAll();
        return stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public StationResponse findByStationId(final Long id) {
        final Station station = stationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(THE_REQUESTED_INFORMATION_DOES_NOT_EXIST));
        return StationResponse.from(station);
    }

    @Transactional
    public void deleteStationById(final Long id) {
        stationRepository.deleteById(id);
    }
}
