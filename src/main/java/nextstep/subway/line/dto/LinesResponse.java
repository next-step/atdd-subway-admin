package nextstep.subway.line.dto;

import java.util.List;

public class LinesResponse {

    private List<LinesResponse> lines;

    protected LinesResponse() {

    }

    public LinesResponse(List<LinesResponse> lines) {
        this.lines = lines;
    }

    public List<LinesResponse> getLines() {
        return lines;
    }
}
