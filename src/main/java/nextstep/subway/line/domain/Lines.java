package nextstep.subway.line.domain;

import nextstep.subway.line.dto.LineFindResponse;

import java.util.ArrayList;
import java.util.List;

public class Lines {

    private List<Line> lines = new ArrayList<>();

    public Lines() {
    }

    public Lines(List<Line> lines) {
        this.lines = lines;
    }

    public static Lines of(List<Line> lines) {
        return new Lines(lines);
    }

    public List<LineFindResponse> toLineFindResponses() {
        List<LineFindResponse> lineFindResponses = new ArrayList<>();
        for (Line line : this.lines) {
            lineFindResponses.add(LineFindResponse.of(line));
        }
        return lineFindResponses;
    }
}
