package nextstep.subway.line.application;

import javax.transaction.Transactional;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class LineService {

    private final LineRepository repository;

    public LineService(LineRepository repository) {
        this.repository = repository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line line = request.convertToLine();
        Line savedLine = repository.save(line);
        return LineResponse.of(savedLine);
    }
}
