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

    public LineResponse modifyLine(Long id, LineRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(()->new LineNotFoundException());
        line.setName(request.getName());
        line.setColor(request.getColor());
        return LineResponse.of(line);
    }

    public LineResponse findLine(Long id) {
        return LineResponse.of(lineRepository.findById(id).orElseThrow(()->new LineNotFoundException()));
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }
}
