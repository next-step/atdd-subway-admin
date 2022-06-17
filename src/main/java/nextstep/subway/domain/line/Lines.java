package nextstep.subway.domain.line;

import java.util.ArrayList;
import java.util.List;

public class Lines {

    private List<Line> lines = new ArrayList<>();

    protected Lines() {
    }

    public Lines(List<Line> lines) {
        this.lines = lines;
    }

    public static Lines create(List<Line> lines) {
        return new Lines(lines);
    }

    public void removeStation(Long stationId) {
        lines.forEach(line -> line.removeSection(stationId));
    }
}