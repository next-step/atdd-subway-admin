package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.domain.Line;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stationResponses = new ArrayList<>();

    public static LineResponse from(Line persistLine) {
        return new LineResponse(persistLine.getId(), persistLine.getName(), persistLine.getColor());
    }

    public LineResponse() {
    }

    public LineResponse(long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineResponse(long id, String name, String color, List<StationResponse> stationResponses) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stationResponses = stationResponses;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationResponse> getStationResponses() {
        return stationResponses;
    }

    public void addStationResponses(StationResponse stationResponse) {
        stationResponses.add(stationResponse);
    }

    public Line toLine() {
        return new Line(id, name, color);
    }
}
