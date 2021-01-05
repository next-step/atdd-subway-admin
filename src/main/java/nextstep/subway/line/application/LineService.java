package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
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

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findById(long id) {
        Line line = find(id);
        return LineResponse.of(line);
    }

    public void update(Long id, LineRequest lineRequest) {
        Line line = find(id);
        line.update(lineRequest.toLine());
    }

    private Line find(long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());
    }

    public void delete(Long id) {
        Line line = find(id);
        lineRepository.delete(line);
    }
}
