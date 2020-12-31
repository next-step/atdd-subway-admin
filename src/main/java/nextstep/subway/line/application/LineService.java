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

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public void updateLine(Long lineId, LineRequest request) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(EntityNotFoundException::new);
        line.update(request.toLine());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long lineId) {
        return LineResponse.of(lineRepository.getOne(lineId));
    }

    public void deleteLineById(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(EntityNotFoundException::new);
        lineRepository.delete(line);
    }
}
