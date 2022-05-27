package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line line = request.convertToLine();
        Line savedLine = lineRepository.save(line);
        return LineResponse.of(savedLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> findLines = lineRepository.findAll();
        return findLines.stream().
                map(line -> LineResponse.of(line)).
                collect(Collectors.toList());
    }
}
