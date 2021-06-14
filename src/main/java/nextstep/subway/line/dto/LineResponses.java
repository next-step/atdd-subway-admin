package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponses {
    private List<LineResponse> lines;

    public LineResponses() {
    }

    public LineResponses(List<LineResponse> lines) {
        this.lines = lines;
    }

    public static LineResponses of(List<Line> lines) {
        List<LineResponse> lineResponses = lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());

        return new LineResponses(lineResponses);
    }

    public List<LineResponse> getLines() {
        return lines;
    }
}
