package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.LineDuplicateException;
import nextstep.subway.line.exception.LineNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        checkDuplicateLine(request);

        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    private void checkDuplicateLine(LineRequest request) {
        if (lineRepository.findByName(request.getName()).isPresent()) {
            throw new LineDuplicateException();
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> persistLines = lineRepository.findAll();
        return LineResponse.ofList(persistLines);
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id)
          .orElseThrow(LineNotFoundException::new);
        return LineResponse.of(line);
    }

    public void update(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
          .orElseThrow(LineNotFoundException::new);
        line.update(lineRequest.toLine());
    }

    public void delete(Long id) {
        lineRepository.deleteById(id);
    }
}
