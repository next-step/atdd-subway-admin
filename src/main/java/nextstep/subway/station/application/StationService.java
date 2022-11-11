package nextstep.subway.station.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.common.exception.DataNotFoundException;
import nextstep.subway.common.message.ExceptionMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StationService {
    private StationRepository stationRepository;

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
                .map(station -> StationResponse.of(station))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    @Transactional
    public Station addToLine(Long id, Line line) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ExceptionMessage.NOT_FOUND_LINE));

        station.addTo(line);

        return station;
    }

    public List<Station> findByLineId(Long lineId) {
        return stationRepository.findByLine_Id(lineId);
    }

    public Map<Long, List<Station>> findStationsByLine(List<Long> lineIds) {
        return stationRepository.findByLine_Id_In(lineIds)
                .stream()
                .collect(Collectors.groupingBy(Station::getLineId, Collectors.toList()));
    }

    @Transactional
    public void removeFromLine(Long lineId) {
        List<Station> stations = findByLineId(lineId);
        stations.forEach(Station::removeFromLine);
    }
}
