package nextstep.subway.line.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nextstep.subway.line.domain.Line;

import java.util.Arrays;
import java.util.List;

public class LineRequest {
    private String name;
    private String color;
    private long upStationId;
    private long downStationId;
    private int distance;

    private LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(String name, String color, long upStationId, long downStationId, int distance) {
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

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    @JsonIgnore
    public List<Long> getStationIds() {
        return Arrays.asList(upStationId, downStationId);
    }

    public Line toLine() {
        return new Line(name, color);
    }
}
