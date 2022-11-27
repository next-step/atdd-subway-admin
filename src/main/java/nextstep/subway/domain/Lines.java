package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Lines {
    private final List<Line> lines;

    public Lines(Line line) {
        this.lines = new ArrayList<>();
        lines.add(line);
    }

    public Lines(List<Line> lines) {
        this.lines = new ArrayList<>(lines);
    }

    public List<Line> asList() {
        return lines;
    }
}
