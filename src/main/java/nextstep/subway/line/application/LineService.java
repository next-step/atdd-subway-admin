package nextstep.subway.line.application;

import nextstep.subway.line.application.exceptions.LineNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.domain.exceptions.StationNotExistException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private SectionRepository sectionRepository;
    private StationService stationService;

    public LineService(
            LineRepository lineRepository, SectionRepository sectionRepository, StationService stationService
    ) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(it -> LineResponse.of(it))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException("해당 라인이 존재하지 않습니다."));

        return LineResponse.of(line);
    }

    public Line updateLine(Long lineId, String changeName, String changeColor) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException("해당 라인이 존재하지 않습니다."));
        Line updateLine = new Line(changeName, changeColor);
        line.update(updateLine);

        return line;
    }

    public void deleteLine(Long lineId) {
        this.getLine(lineId);
        lineRepository.deleteById(lineId);
    }
}
