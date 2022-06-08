package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineRequest {
    private final String name;
    private final String color;
    private final SectionRequest sectionRequest;

    public LineRequest(String name, String color, Long upStationId, Long downStationId, Long distance) {
        this.name = name;
        this.color = color;
        this.sectionRequest = new SectionRequest(upStationId, downStationId, distance);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public SectionRequest getSectionRequest() {
        return sectionRequest;
    }

    public Line toLine() {
        return new Line(name, color);
    }
}
