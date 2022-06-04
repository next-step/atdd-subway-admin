package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineStationRequest;
import nextstep.subway.dto.LineStationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineStationService {
    private final LineStationRepository lineStationRepository;
    private final LineService lineService;
    private final StationService stationService;

    public LineStationService(LineStationRepository lineStationRepository, LineService lineStationService, StationService stationService) {
        this.lineStationRepository = lineStationRepository;
        this.lineService = lineStationService;
        this.stationService = stationService;
    }

    public LineStationResponse createLineStation(LineStationRequest lineStationRequest) {
        Line line = lineService.findLineById(lineStationRequest.getLineId());

        Station upStation = stationService.findStationById(lineStationRequest.getUpStationId());
        Station downStation = stationService.findStationById(lineStationRequest.getDownStationId());
        LineStation newLineStation = LineStation.of(upStation, downStation, lineStationRequest.getDistance());

        line.addLineStation(newLineStation);
        
        return new LineStationResponse();
    }

    private LineStation saveLineStation(LineStation lineStation) {
        return lineStationRepository.save(lineStation);
    }
}
