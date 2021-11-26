package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.util.ArrayList;
import java.util.List;

public class LinesResponse {
    private List<LineResponse> lines;

    public LinesResponse() {
        this.lines = new ArrayList<>();
    }

    public LinesResponse(List<Line> lines) {
        this();
        for (Line line : lines) {
            this.lines.add(LineResponse.of(line));
        }
    }

    public List<LineResponse> getLines() {
        return lines;
    }

    public void setLines(List<LineResponse> lines) {
        this.lines = lines;
    }
}
