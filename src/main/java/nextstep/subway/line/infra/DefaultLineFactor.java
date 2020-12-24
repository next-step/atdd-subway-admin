package nextstep.subway.line.infra;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineFactory;
import nextstep.subway.line.dto.LineRequest;
import org.springframework.stereotype.Component;

@Component
public class DefaultLineFactor implements LineFactory {
    @Override
    public Line create(final LineRequest lineRequest) {
        return null;
    }
}
