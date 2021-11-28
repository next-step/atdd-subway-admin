package nextstep.subway.line.application;


import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineStationRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationSection;
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

        StationSection stationSection = stationService.findStationSection(
            lineStationRequest.getUpStationId(), lineStationRequest.getDownStationId());

        LineStation lineStation = LineStation.of(stationSection,
            Distance.of(lineStationRequest.getDistance()));
        line.addLineStation(lineStation);
    }

    public void removeSectionByStationId(Long lineId, Long deleteStationId) {
        Line line = lineService.findLine(lineId);
        Station deleteStation = stationService.findStation(deleteStationId);

        line.removeLineStation(deleteStation.getId());
    }
}
