package nextstep.subway.line.application;


import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineStationRequest;
import nextstep.subway.station.application.StationService;
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
        stationService.validateSavers(lineStationRequest.getUpStationId(),
            lineStationRequest.getDownStationId());
        Line line = lineService.findLine(lineId);

        LineStation lineStation = LineStation.of(lineStationRequest.getUpStationId(),
            lineStationRequest.getDownStationId(), lineStationRequest.getDistance());
        line.addLineStation(lineStation);
    }
}
