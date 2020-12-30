package nextstep.subway.line.application;

import nextstep.subway.advice.exception.LineNotFoundException;
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

    public void deleteLine(Long id) {
        lineRepository.delete(getLindById(id));
    }

    public LineResponse modifyLine(Long id, LineRequest request) {
        Line line = getLindById(id);
        line.updateName(request.getName());
        line.updateColor(request.getColor());
        return LineResponse.of(line);
    }

    public LineResponse findLine(Long id) {
        return LineResponse.of(getLindById(id));
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    private Line getLindById(Long id) {
        return lineRepository.findById(id).orElseThrow(()->new LineNotFoundException(id));
    }
}
