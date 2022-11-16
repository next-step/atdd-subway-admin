package nextstep.subway.domain;

import nextstep.subway.dto.LineRequest;

import javax.persistence.*;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String color;

    @Embedded
    private LineStations lineStations = new LineStations();

    protected Line() {}

    public Line(String name, String color, LineStations lineStations) {
        this.name = name;
        this.color = color;
        this.lineStations = lineStations;
    }

    public Line updateInfo(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
        return this;
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

    public LineStations getLineStations() {
        return lineStations;
    }

}
