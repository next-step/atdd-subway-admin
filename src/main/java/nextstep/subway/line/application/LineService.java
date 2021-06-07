package nextstep.subway.line.application;

import nextstep.subway.exception.DuplicateValueException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    public static final String DUPLICATE_VALUE_ERROR_MESSAGE = "%s 라인은 이미 등록된 라인 합니다.";
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        try {
            Line persistLine = lineRepository.save(request.toLine());
            return LineResponse.of(persistLine);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateValueException(String.format(DUPLICATE_VALUE_ERROR_MESSAGE, request.getName()));
        }
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Optional<Line> findLine = lineRepository.findById(id);
        Line line = Line.getNotNullLine(findLine);
        return LineResponse.of(line);
    }

    public void updateLineById(Long id, LineRequest lineRequest) {
        Optional<Line> findLine = lineRepository.findById(id);
        Line line = Line.getNotNullLine(findLine);
        line.update(lineRequest.toLine());
    }

    public void deleteLineById(Long id) {
        Optional<Line> findLine = lineRepository.findById(id);
        Line line = Line.getNotNullLine(findLine);
        lineRepository.delete(line);
    }
}
