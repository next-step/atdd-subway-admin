package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.exception.DataNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final StationService stationService;
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationService.findByIdOrElseThrow(lineRequest.getUpStationId());
        Station downStation = stationService.findByIdOrElseThrow(lineRequest.getDownStationId());
        Line line = new Line(lineRequest.getName(), lineRequest.getColor(),
                upStation, downStation, lineRequest.getDistance());
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> line = lineRepository.findAll();
        return line.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse getLineResponseById(Long id) {
        Line line = findById(id);
        return LineResponse.of(line);
    }

    @Transactional
    public Line changeLineById(Long id, String name, String color) {
        Line line = findById(id);
        line.change(name, color);
        return lineRepository.save(line);
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = findById(id);
        lineRepository.delete(line);
    }

    private Line findById(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new DataNotFoundException("노선이 존재하지 않습니다."));
    }
}
