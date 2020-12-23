package nextstep.subway.line.application;

import nextstep.subway.line.application.exceptions.LineNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
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

    public LineResponse updateLine(Long lineId, String changeName, String changeColor) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException("해당 라인이 존재하지 않습니다."));
        Line updateLine = new Line(changeName, changeColor);
        line.update(updateLine);

        return LineResponse.of(updateLine);
    }

    public void deleteLine(Long lineId) {
        this.getLine(lineId);
        lineRepository.deleteById(lineId);
    }
}
