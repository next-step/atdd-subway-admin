package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponses {

    private List<LineResponse> lineResponses;

    public LineResponses() {
    }

    public LineResponses(final List<Line> list) {
        this.lineResponses = list.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public List<LineResponse> getLineResponses() {
        return lineResponses;
    }

    public int size() {
        return lineResponses.size();
    }

}
