package nextstep.subway.line.application;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getLines() {
        List<Line> lines = lineRepository.findAll();
        return convertToLineResponses(lines);
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(Long id) {
        Line findLine = lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("not found line id : " + id));
        return LineResponse.of(findLine);
    }

    private List<LineResponse> convertToLineResponses(List<Line> lines) {
        List<LineResponse> responseLines = new ArrayList<>();
        for (Line line : lines) {
            responseLines.add(LineResponse.of(line));
        }
        return responseLines;
    }
}
