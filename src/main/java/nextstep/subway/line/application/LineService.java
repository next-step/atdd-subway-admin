package nextstep.subway.line.application;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineFactory;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LineService {

    private final LineRepository lineRepository;

    private final LineFactory lineFactory;

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(lineFactory.create(request));
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getLines() {
        List<Line> persistLines = lineRepository.findAll();
        return LineResponse.ofList(persistLines);
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(final Long id) {
        Line persistLine = getPersistLine(id);
        return LineResponse.of(persistLine);
    }

    @Transactional
    public LineResponse updateLine(final Long id, final LineRequest request) {
        Line persistLine = getPersistLine(id);
        persistLine.update(lineFactory.create(request));
        return LineResponse.of(persistLine);
    }

    @Transactional
    public void deleteLine(final Long id) {
        Line persistLine = getPersistLine(id);
        lineRepository.delete(persistLine);
    }

    private Line getPersistLine(final Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("노선이 존재하지 않습니다. (입력 id 값: %d)", id)));
    }
}
