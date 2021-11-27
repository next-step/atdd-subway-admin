package nextstep.subway.line.application;


import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineStationRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineStationService {

    private final LineService lineService;
    private final StationService stationService;

    public LineStationService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public void createLineStation(Long lineId, LineStationRequest lineStationRequest) {
        Line line = lineService.findLine(lineId);

        Station station = stationService.findStation(lineStationRequest.getUpStationId());
        Station nextStation = stationService.findStation(lineStationRequest.getDownStationId());

        LineStation lineStation = LineStation.of(station.getId(), nextStation.getId(),
            Distance.of(lineStationRequest.getDistance()));
        line.addLineStation(lineStation);
    }

    public void removeSectionByStationId(Long lineId, Long deleteStationId) {
        Line line = lineService.findLine(lineId);
        Station deleteStation = stationService.findStation(deleteStationId);

        line.removeLineStation(deleteStation.getId());
    }
}
