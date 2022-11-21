package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lines {

    private List<Line> lines = new ArrayList<>();

    protected Lines() {
    }

    public Lines(List<Line> lines) {
        this.lines = lines;
    }

    public static Lines of(List<Line> lines) {
        return new Lines(lines);
    }

    private boolean contains(Long lineId) {
        return lines.stream()
                .anyMatch(line -> Objects.equals(line.getId(), lineId));
    }
}
