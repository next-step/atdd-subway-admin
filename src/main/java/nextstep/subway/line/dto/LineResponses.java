package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LineResponses {
    private final List<LineResponse> lineResponses;

    private LineResponses(){
        lineResponses = new ArrayList<>();
    }

    private LineResponses(List<LineResponse> lineResponses) {
        this.lineResponses = lineResponses;
    }

    public static LineResponses of(List<Line> lines) {
        return new LineResponses(lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList()));
    }

    public List<LineResponse> toList() {
        return new ArrayList<>(lineResponses);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineResponses that = (LineResponses) o;
        return Objects.equals(lineResponses, that.lineResponses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineResponses);
    }
}
