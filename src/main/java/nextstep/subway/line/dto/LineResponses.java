package nextstep.subway.line.dto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;

public class LineResponses {

    private final List<LineResponse> lineResponses;

    private LineResponses(final List<LineResponse> lineResponses) {
        this.lineResponses = lineResponses;
    }

    public static LineResponses of(final List<Line> lines) {
        return new LineResponses(
            lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList))
        );
    }

    public List<LineResponse> getLineResponses() {
        return Collections.unmodifiableList(lineResponses);
    }
}
