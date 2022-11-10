package nextstep.subway.domain;

import nextstep.subway.dto.LineUpdateRequest;

import javax.persistence.*;

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
    private final LineStations lineStations = new LineStations();

    protected Line() {
    }

    public Line(String name, String color, int distance) {
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public void update(LineUpdateRequest lineUpdateRequest) {
        if (lineUpdateRequest.getName() != null) {
            this.name = lineUpdateRequest.getName();
        }
        if (lineUpdateRequest.getColor() != null) {
            this.color = lineUpdateRequest.getColor();
        }
    }

    public void addStation(Station station) {
        lineStations.add(station);
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
