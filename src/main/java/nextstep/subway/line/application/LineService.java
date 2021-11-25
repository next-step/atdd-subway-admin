package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.exception.LineDuplicateException;
import nextstep.subway.exception.NotFoundLineException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@Service
@Transactional
public class LineService {
    public static final int LINES_EMPTY = 0;
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        saveLineValidation(request);
        Line line = lineRepository.save(request.toLine());
        return LineResponse.of(line);
    }

    private void saveLineValidation(LineRequest request) {
        if (isDuplicate(request.getName())) {
            throw new LineDuplicateException();
        }
    }

    private boolean isDuplicate(String name) {
        return lineRepository.findByName(name).isPresent();
    }

    @Transactional()
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        findAllLinesValidation(lines);
        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    private void findAllLinesValidation(List<Line> lines) {
        if (isLinesEmpty(lines)) {
            throw new NotFoundLineException();
        }
    }

    private boolean isLinesEmpty(List<Line> lines) {
        return lines.size() <= LINES_EMPTY;
    }

    @Transactional(readOnly = true)
    public LineResponse findId(Long id) {
        Line line = lineRepository.findById(id)
            .orElseThrow(NotFoundLineException::new);
        return LineResponse.of(line);
    }

    public LineResponse update(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
            .orElseThrow(NotFoundLineException::new);
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public void delete(Long id) {
        lineRepository.deleteById(id);
    }

}
