package nextstep.subway.application;

import nextstep.subway.domain.*;
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

    public LineStationResponse createLineStation(Long lineId, LineStationRequest lineStationRequest) {
        Station upStation = stationService.findStationById(lineStationRequest.getUpStationId());
        Station downStation = stationService.findStationById(lineStationRequest.getDownStationId());
        LineStation newLineStation = LineStation.of(upStation, downStation, new Distance(lineStationRequest.getDistance()));

        Line line = lineService.findLineById(lineId);
        line.addLineStation(newLineStation);

        return LineStationResponse.of(saveLineStation(newLineStation));
    }

    private LineStation saveLineStation(LineStation lineStation) {
        return lineStationRepository.save(lineStation);
    }
}
