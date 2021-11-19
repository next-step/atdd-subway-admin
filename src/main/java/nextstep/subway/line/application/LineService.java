package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        final Optional<Line> optionalLine = readById(id);
        return LineResponse.of(optionalLine.orElse(null));
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        final Optional<Line> optionalLine = readById(id);

        optionalLine.ifPresent(line -> line.update(lineRequest.toLine()));

        return LineResponse.of(optionalLine.orElse(null));
    }

    private Optional<Line> readById(Long id) {
        return lineRepository.findById(id);
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }
}
