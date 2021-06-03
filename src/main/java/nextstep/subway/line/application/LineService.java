package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
public class LineService {
    private static final String LINE_NOT_FOUND = "존재하지 않는 노선입니다. ";

    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest request) {
        final Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(line -> LineResponse.of(line))
                .collect(toList());
    }

    public LineResponse findLineById(final Long id) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException(LINE_NOT_FOUND + id));
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(final Long id, final LineRequest lineRequest) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException(LINE_NOT_FOUND + id));
        line.update(lineRequest.toLine());
    }

    @Transactional
    public void deleteLineById(final Long id) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException(LINE_NOT_FOUND + id));
        lineRepository.delete(line);
    }
}
