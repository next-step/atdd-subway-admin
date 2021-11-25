package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.util.ArrayList;
import java.util.List;

public class LinesResponse {
    private List<LineResponse> linesResponse;

    public LinesResponse() {
        this.linesResponse = new ArrayList<>();
    }

    public LinesResponse(List<Line> lines) {
        this();
        for(Line line : lines){
            linesResponse.add(LineResponse.of(line));
        }
    }

    public List<LineResponse> getLinesResponse() {
        return linesResponse;
    }

    public void setLinesResponse(List<LineResponse> linesResponse) {
        this.linesResponse = linesResponse;
    }
}
