package nextstep.subway.line.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;

import java.util.Arrays;
import java.util.List;

public class LineRequest {
    private String name;
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
        this.name = name;
        this.color = color;
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

    @JsonIgnore
    public List<Long> getStationIds() {
        return Arrays.asList(upStationId, downStationId);
    }

    public int getDistance() {
        return distance;
    }

    public Line toLine() {
        Line line = new Line(name, color);
        line.addSection(new Section(distance));
        return line;
    }
}
