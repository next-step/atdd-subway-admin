package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private int distance;
    private StationResponse upStation;
    private StationResponse downStation;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, int distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getDistance());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void setUpStation(StationResponse upStation) {
        this.upStation = upStation;
    }

    public void setDownStation(StationResponse downStation) {
        this.downStation = downStation;
    }
}
