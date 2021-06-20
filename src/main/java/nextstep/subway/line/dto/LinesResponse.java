package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

public class LinesResponse {
    private List<LineResponse> lines;

    public LinesResponse() {
    }

    public LinesResponse(List<LineResponse> lines) {
        this.lines = lines;
    }

    public static LinesResponse of(List<Line> lines) {
        List<LineResponse> lineResponses = lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());

        return new LinesResponse(lineResponses);
    }

    public List<LineResponse> getLines() {
        return lines;
    }
}
