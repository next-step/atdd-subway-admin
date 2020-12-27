package nextstep.subway.line.application;

import java.util.Optional;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
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
        if (isExistLine(request)) {
            return LineResponse.fail();
        }

        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    private boolean isExistLine(LineRequest request) {
        Optional<Line> maybeLine = findLine(request.toLine());
        return maybeLine.isPresent();
    }

    private Optional<Line> findLine(Line line) {
        return lineRepository.findByName(line.getName());
    }
}
