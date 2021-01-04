package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private LineStations lineStations = new LineStations();

    protected Line() {
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(String name, String color, Long upStationId, Long downStationId, int distance) {
        this(null, name, color, upStationId, downStationId, distance);
    }

    public Line(Long id, String name, String color, Long upStationId, Long downStationId, int distance) {
        this(id, name, color);
        lineStations.initLineStation(upStationId, downStationId, distance);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
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

    public void addLineStation(Long upStationId, Long downStationId, int distance) {
        if (upStationId == null || downStationId == null)
            return;

        lineStations.addLineStation(upStationId, downStationId, distance);
    }

    public void removeLineStation(Long stationId) {

    }
}
