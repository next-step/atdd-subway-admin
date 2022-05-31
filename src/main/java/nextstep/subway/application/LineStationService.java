package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.CreateLineStationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineStationService {
    private final LineStationRepository lineStationRepository;
    private final StationService stationService;

    public LineStationService(LineStationRepository lineStationRepository, StationService stationService) {
        this.lineStationRepository = lineStationRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineStation saveLineStation(CreateLineStationRequest request, Line line) {
        Station upStation = stationService.getOrElseThrow(request.getUpStationId());
        Station downStation = stationService.getOrElseThrow(request.getDownStationId());

        LineStation lineStation = lineStationRepository.save(new LineStation(request.getDistance(), upStation, downStation));

        line.addLineStation(lineStation);
        return lineStation;
    }
}
