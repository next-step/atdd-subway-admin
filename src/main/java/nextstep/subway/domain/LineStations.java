package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;

public class LineStations {
    private final List<Section> sectionElement;

    public LineStations(List<Section> sectionElement) {
        this.sectionElement = sectionElement;
    }

    public static LineStations createLineStations(Section... sections) {
        return new LineStations(Arrays.asList(sections));
    }
}
