package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getLines() {
        List<Line> persistLines = lineRepository.findAll();
        return persistLines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(final Long id) {
        Line persistLine = getPersistLine(id);
        return LineResponse.of(persistLine);
    }

    @Transactional
    public LineResponse updateLine(final Long id, final LineRequest request) {
        Line persistLine = getPersistLine(id);
        persistLine.update(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional
    public void deleteLine(final Long id) {
        Line persistLine = getPersistLine(id);
        lineRepository.delete(persistLine);
    }

    private Line getPersistLine(final Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("해당 노선이 없습니다. (입력 값:%d)", id)));
    }
}
