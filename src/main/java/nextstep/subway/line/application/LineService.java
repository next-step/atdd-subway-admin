package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.line.exception.NameDuplicateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        checkName(request.getName());
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    private void checkName(String name) {
        Line existingLine = lineRepository.findByName(name);
        if (existingLine != null) {
            throw new NameDuplicateException("이미 존재하는 이름입니다. : " + name);
        }
    }

    public LineResponse update(LineRequest request, Long id) {
        Line line = line(id);
        checkName(request.getName());
        line.update(request.toLine());
        return LineResponse.of(line);
    }

    public List<LineResponse> getLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
            .map(line -> LineResponse.of(line))
            .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line line = line(id);
        return LineResponse.of(line);
    }

    public void delete(Long id) {
        Line line = line(id);
        lineRepository.delete(line);
    }

    private Line line(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new LineNotFoundException(id + " : 존재하지 않는 라인입니다."));
    }
}
