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

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Optional<Line> findLine = lineRepository.findById(id);
        Line.checkNullLine(findLine);
        return LineResponse.of(findLine.get());
    }

    public void updateLineById(Long id, LineRequest lineRequest) {
        Optional<Line> findLine = lineRepository.findById(id);
        Line.checkNullLine(findLine);
        findLine.get().update(lineRequest.toLine());
    }
}
