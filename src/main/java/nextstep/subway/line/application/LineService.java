package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
    public LineResponse findLineById(Long id) {
        Optional<Line> line = lineRepository.findById(id);
        return LineResponse.of(line.get());
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse updateLineById(Long id, LineRequest lineRequest) {
        Optional<Line> line = lineRepository.findById(id);
        Line updateLine = new Line(lineRequest.getName(), lineRequest.getColor());
        line.get().update(updateLine);
        return LineResponse.of(line.get());
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

}
