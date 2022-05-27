package nextstep.subway.domain;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.dto.LineResponse;

public final class Lines {
    private final List<Line> lines;

    private Lines(List<Line> lines) {
        this.lines = lines;
    }

    public static Lines of(List<Line> lines) {
        return new Lines(lines);
    }

    public List<LineResponse> toLineResponse() {
        return this.lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }
}
