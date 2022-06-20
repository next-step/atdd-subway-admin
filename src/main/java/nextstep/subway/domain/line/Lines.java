package nextstep.subway.domain.line;

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

    public static Lines create(List<Line> lines) {
        return new Lines(lines);
    }

    public void removeStation(Long lineId, Long stationId) {
        if (contains(lineId)) {
            throw new IllegalArgumentException("노선에 포함되지 않는 역입니다. lineId : " + lineId + ", stationId : " + stationId);
        }

        lines.forEach(line -> line.removeSection(stationId));
    }

    private boolean contains(Long lineId) {
        return lines.stream()
                .anyMatch(line -> Objects.equals(line.getId(), lineId));
    }
}
