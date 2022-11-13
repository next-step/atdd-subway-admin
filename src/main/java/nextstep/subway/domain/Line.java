package nextstep.subway.domain;

import nextstep.subway.dto.LineUpdateRequest;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String color;

    @Column(name = "distance", nullable = false)
    private int distance;

    @Embedded
    private LineStations lineStations = new LineStations();

    protected Line() {
    }

    public Line(String name, String color, int distance) {
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public void initLineStations(List<LineStation> lineStations) {
        this.lineStations = new LineStations(lineStations);
    }

    public void update(LineUpdateRequest lineUpdateRequest) {
        if (lineUpdateRequest.getName() != null) {
            this.name = lineUpdateRequest.getName();
        }
        if (lineUpdateRequest.getColor() != null) {
            this.color = lineUpdateRequest.getColor();
        }
    }

    public void addLineStation(LineStation lineStation) {
        lineStations.add(lineStation);
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

    public int getDistance() {
        return distance;
    }

    public LineStations getLineStation() {
        return lineStations;
    }
}
