package nextstep.subway.station.application;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.dto.StationResponseList;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        validateDuplicatedStation(stationRequest.getName());
        Station persistStation = stationRepository.save(stationRequest.toStation());
        return StationResponse.of(persistStation);
    }

    private void validateDuplicatedStation(String name) {
        Optional<Station> exist = stationRepository.findByName(name);
        if (exist.isPresent()) {
            throw new DataIntegrityViolationException("중복된 역을 추가할 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public StationResponseList findAllStations() {
        List<Station> stations = stationRepository.findAll();
        return new StationResponseList(stations);
    }

    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }
}
