package nextstep.subway.line.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nextstep.subway.line.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

public class LinesResponse {

    private List<LineResponse> lineResponses;

    public LinesResponse(List<LineResponse> lineResponses) {
        this.lineResponses = lineResponses;
    }

    public static LinesResponse from(List<Line> lines) {
        List<LineResponse> lineResponses = lines.stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());

        return new LinesResponse(lineResponses);
    }

    public int size() {
        return this.lineResponses.size();
    }

    @JsonIgnore
    public boolean isEmpty() {
        return this.lineResponses.isEmpty();
    }

    public List<LineResponse> getLineResponses() {
        return lineResponses;
    }
}
