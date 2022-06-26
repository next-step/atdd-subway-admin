package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineStations;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<LineStationResponse>  lineStations = new ArrayList<>();

    public static LineResponse from(Line persistLine) {
        return new LineResponse(persistLine.getId(), persistLine.getName(), persistLine.getColor(),
                persistLine.getLineStations());
    }

    public LineResponse() {
    }

    public LineResponse(long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineResponse(long id, String name, String color, LineStations lineStations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.lineStations = LineStationResponse.to(lineStations);
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

    public List<LineStationResponse> getLineStations() {
        return lineStations;
    }

    public Line toLine() {
        return new Line(id, name, color, lineStations);
    }
}
