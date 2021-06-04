package nextstep.subway.line.application;

import java.util.List;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public Line saveLine(Line line) {
        checkAlreadyExists(line.getName());
        return lineRepository.save(line);
    }

    @Transactional(readOnly = true)
    public List<Line> findAllLines() {
        return lineRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Line findLineById(Long lineId) {
        return lineRepository.findById(lineId)
            .orElseThrow(IllegalArgumentException::new);
    }

    public void updateLineById(Long lineId, Line updateLine) {
        Line line = findLineById(lineId);
        line.update(updateLine);
    }

    private void checkAlreadyExists(String name) {
        if (lineRepository.existsByName(name)) {
            throw new AlreadyExistsLineNameException(String.format("노선 이름이 이미 존재합니다.[%s]", name));
        }
    }
}
