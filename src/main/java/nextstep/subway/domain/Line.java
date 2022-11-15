package nextstep.subway.domain;

import nextstep.subway.dto.LineRequest;

import javax.persistence.*;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Stations stations = new Stations();

    private String name;
    private String color;
    private int distance;

    protected Line() {}

    public Line(String name, String color, int distance) {
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public void addStation(Station station) {
        station.toLine(this);
        stations.add(station);
    }

    public Line updateInfo(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
        return this;
    }

    public Long getId() {
        return id;
    }

    public Stations getStations() {
        return stations;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

}
