package nextstep.subway.line.apllication;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;

@Service
public class LineService {
    public LineResponse create(LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        return null;
    }
}
