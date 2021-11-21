package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineResponses;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(final LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public LineResponses findAllLines() {
        return LineResponses.of(lineRepository.findAll());
    }

    public LineResponse findLineById(final long id) {
        return LineResponse.of(getLine(id));
    }

    public LineResponse updateLine(final long id, final LineRequest request) {
        final Line line = getLine(id);

        line.update(request.toLine());

        return LineResponse.of(line);
    }

    private Line getLine(final long id) {
        return lineRepository.findById(id).orElseThrow(IllegalStateException::new);
    }
}
