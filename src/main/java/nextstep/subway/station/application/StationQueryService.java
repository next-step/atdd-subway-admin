package nextstep.subway.station.application;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StationQueryService {
    private StationRepository stationRepository;

    public StationQueryService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public List<Station> findByLineId(Long lineId) {
        return stationRepository.findByLine_Id(lineId);
    }

    public Map<Long, List<Station>> findStationsByLine(List<Long> lineIds) {
        return stationRepository.findByLine_Id_In(lineIds)
                .stream()
                .collect(Collectors.groupingBy(Station::getLineId, Collectors.toList()));
    }
}
