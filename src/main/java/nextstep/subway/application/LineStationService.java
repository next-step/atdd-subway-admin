package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineStationRequest;
import nextstep.subway.dto.LineStationResponse;
import nextstep.subway.exception.NotFoundException;
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
        LineStation newLineStation = LineStation.of(upStation, downStation, lineStationRequest.getDistance());

        Line line = lineService.findLineById(lineId);
        line.addLineStation(newLineStation);

        return LineStationResponse.of(saveLineStation(newLineStation));
    }

    private LineStation saveLineStation(LineStation lineStation) {
        return lineStationRepository.save(lineStation);
    }

    public void deleteLineStation(Long lineStationId) {
        lineStationRepository.delete(findLineStationById(lineStationId));
    }

    private LineStation findLineStationById(Long lineStationId) {
        return lineStationRepository.findById(lineStationId)
                .orElseThrow(() -> new NotFoundException("지하철구간을 찾을 수 없습니다."));
    }
}
