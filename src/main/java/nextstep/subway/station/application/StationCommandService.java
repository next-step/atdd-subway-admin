package nextstep.subway.station.application;

import nextstep.subway.common.exception.DataNotFoundException;
import nextstep.subway.common.message.ExceptionMessage;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StationCommandService {
    private final StationRepository stationRepository;

    public StationCommandService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        Station persistStation = stationRepository.save(stationRequest.toStation());
        return StationResponse.from(persistStation);
    }

    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    public Station addToLine(Long id, Line line) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(ExceptionMessage.NOT_FOUND_LINE));

        station.addTo(line);

        return station;
    }

    public void removeFromLine(Long lineId) {
        List<Station> stations = stationRepository.findByLine_Id(lineId);
        stations.forEach(Station::removeFromLine);
    }
}
