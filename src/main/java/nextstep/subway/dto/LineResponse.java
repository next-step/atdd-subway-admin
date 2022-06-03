package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private final Long id;

    private final String name;

    private final String color;

    private final List<LineStationResponse> lineStation;

    public LineResponse(Long id, String name, String color, List<LineStationResponse> lineStation) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.lineStation = lineStation;
    }

    public static LineResponse of(Line line) {
        List<LineStationResponse> lineStations = line.getLineStations()
                .stream()
                .map(LineStationResponse::of)
                .collect(Collectors.toList());

        return new LineResponse(line.getId(), line.getName(), line.getColor(), lineStations);
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

    public List<LineStationResponse> getLineStation() {
        return lineStation;
    }
}
