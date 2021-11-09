package nextstep.subway.station.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.common.domain.Name;
import nextstep.subway.common.exception.DuplicateDataException;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StationService {

    private final StationRepository repository;

    public StationService(StationRepository repository) {
        this.repository = repository;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        validateDuplicateName(stationRequest.name());
        return StationResponse.of(savedStation(stationRequest));
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStations() {
        return repository.findAll()
            .stream()
            .map(StationResponse::of)
            .collect(Collectors.toList());
    }

    public void deleteStationById(Long id) {
        repository.deleteById(id);
    }

    public Station findStation(long id) {
        return repository.findById(id)
            .orElseThrow(
                () -> new NotFoundException(String.format("station id(%d) does not exist", id)));
    }

    private void validateDuplicateName(Name name) {
        if (repository.existsByName(name)) {
            throw new DuplicateDataException(String.format("Name(%s) already exists", name));
        }
    }

    private Station savedStation(StationRequest stationRequest) {
        return repository.save(stationRequest.toStation());
    }
}
