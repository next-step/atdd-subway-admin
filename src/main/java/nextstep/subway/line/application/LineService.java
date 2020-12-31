package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;

@Service
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse save(LineRequest lineRequest) {
        Line line = lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        LineResponse response = new LineResponse(line.getId(), line.getName(), line.getColor());
        return response;
    }
}
