package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.dto.SectionRequest;

import javax.validation.constraints.NotBlank;

public class LineRequest {
    @NotBlank(message = "노선명을 입력하세요")
    private String name;
    @NotBlank(message = "노선 색상을 입력하세요")
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this(name, color);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Line toLine() {
        return new Line(name, color);
    }

    public boolean hasSectionArguments() {
        return upStationId != null ||
                downStationId != null ||
                distance > 0;
    }

    public SectionRequest toSectionRequest() {
        return SectionRequest.of(this);
    }
}
