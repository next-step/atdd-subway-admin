package nextstep.subway.station.application;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationNotFoundException;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

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
    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

	public Optional<Station> findById(Long id) {
        return stationRepository.findById(id);
	}

    public Station getById(Long upStationId) throws StationNotFoundException {
        try {
            return stationRepository.getOne(upStationId);
        } catch (EntityNotFoundException e) {
            throw new StationNotFoundException(upStationId, e);
        }
    }
}
