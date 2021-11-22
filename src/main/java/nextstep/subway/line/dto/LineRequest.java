package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;

public class LineRequest {
    private final String name;
    private final String color;
    private final SectionRequest sectionRequest;

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        sectionRequest = new SectionRequest(upStationId, downStationId, distance);
    }

    public String getName() {
        return name;
    }

    public Line toLine(Section section) {
        return Line.of(name, color, section);
    }

    public SectionRequest getSectionRequest() {
        return sectionRequest;
    }
}
