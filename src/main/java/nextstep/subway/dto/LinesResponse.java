package nextstep.subway.dto;

import java.util.List;

public class LinesResponse {

    private final List<LineResponse> linesResponse;

    public LinesResponse(List<LineResponse> linesResponse) {
        this.linesResponse = linesResponse;
    }

    public List<LineResponse> getLinesResponse() {
        return linesResponse;
    }
}
