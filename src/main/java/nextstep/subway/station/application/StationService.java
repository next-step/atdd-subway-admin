package nextstep.subway.station.application;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.domain.exceptions.StationNotExistException;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationInfo saveStation(StationRequest stationRequest) {
        Station persistStation = stationRepository.save(stationRequest.toStation());
        return StationInfo.of(persistStation);
    }

    @Transactional(readOnly = true)
    public List<StationInfo> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(station -> StationInfo.of(station))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Station getStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationNotExistException("존재하지 않는 역입니다."));
    }

    @Transactional(readOnly = true)
    public List<Station> getStations(List<Long> ids) {
        return stationRepository.findAllById(ids);
    }

    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }
}
