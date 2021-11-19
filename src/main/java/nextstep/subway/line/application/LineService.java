package nextstep.subway.line.application;

import nextstep.subway.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<LineResponse> readAllLines() {
        final List<Line> lines = lineRepository.findAll();
        return LineResponse.of(lines);
    }

    @Transactional(readOnly = true)
    public LineResponse readLine(Long id) {
        final Line line = lineRepository.findById(id)
                                        .orElseThrow(() -> new NotFoundException("해당하는 Line이 없습니다. id = " + id));
        return LineResponse.of(line);
    }
}
