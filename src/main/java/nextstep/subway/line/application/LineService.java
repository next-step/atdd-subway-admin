package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.NotFoundLineException;
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
    public List<LineResponse> getLines() {
        List<Line> lines = lineRepository.findAll();
        return LineResponse.ofList(lines);
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(Long id) {
        Line line = findById(id);
        return LineResponse.of(line);
    }

    public void update(Long id, LineRequest lineRequest) {
        Line line = findById(id);
        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundLineException(id + " 노선이 없습니다."));
    }

    public void delete(Long id) {
        lineRepository.deleteById(id);
    }
}
