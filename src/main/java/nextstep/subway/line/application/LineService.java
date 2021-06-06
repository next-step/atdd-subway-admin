package nextstep.subway.line.application;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    public LineService(final LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(final LineRequest request) {
        final Line persistLine = lineRepository.save(request.toLine());

        return lineResponse(persistLine);
    }

    private LineResponse lineResponse(final Line line) {
        return LineResponse.of(line);
    }

    public List<LineResponse> findLines() {
        final List<Line> lines = findAllLine();
        final List<LineResponse> lineResponses = new ArrayList<>(lines.size());
        lines.forEach(line -> lineResponses.add(lineResponse(line)));

        return lineResponses;
    }

    private List<Line> findAllLine() {
        return lineRepository.findAll();
    }

    public LineResponse findLine(final Long id) {
        return LineResponse.of(findById(id));
    }

    private Line findById(final Long id) {
        return lineRepository.findById(id)
            .orElseThrow(NotFoundException::new);
    }
}
