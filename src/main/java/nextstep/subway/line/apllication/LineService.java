package nextstep.subway.line.apllication;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;

@Service
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse create(LineRequest lineRequest) {
        Line line = lineRepository.save(lineRequest.toLine());
        return LineResponse.of(line);
    }
}
