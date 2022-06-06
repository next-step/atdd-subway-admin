package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.CreateLineStationRequest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineStationRequest;
import nextstep.subway.exception.CustomException;
import nextstep.subway.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final LineStationService lineStationService;

    public LineService(LineRepository lineRepository, LineStationService lineStationService) {
        this.lineRepository = lineRepository;
        this.lineStationService = lineStationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        addLineStation(lineRequest, line);

        return LineResponse.of(line);
    }

    private void addLineStation(CreateLineStationRequest createLineStationRequest, Line line) {
        LineStation lineStation = lineStationService.saveLineStation(createLineStationRequest);
        line.addLineStation(lineStation);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findByLine(Long id) {
        Line line = getOrElseThrow(id);

        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = getOrElseThrow(id);
        line.update(lineRequest);
    }

    @Transactional
    public void deleteStationById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void createLineStation(Long id, LineStationRequest lineStationRequest) {
        Line line = getOrElseThrow(id);
        addLineStation(lineStationRequest, line);
    }


    private Line getOrElseThrow(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.LINE_NOT_FOUND));
    }

    @Transactional
    public void deleteLineStationByStationId(Long id, Long stationId) {
        Line line = getOrElseThrow(id);
        Station station = lineStationService.getStation(stationId);
        line.deleteStation(station);
    }
}
