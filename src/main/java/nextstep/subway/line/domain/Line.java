package nextstep.subway.line.domain;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
@Table(name = "line")
public class Line extends BaseEntity {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "color")
    private String color;

    @Embedded
    private LineStations lineStations = new LineStations();

    public void addSection(Station upStation, Station downStation, int distance) {
        lineStations.add(LineStation.of(upStation, null, 0));
        lineStations.add(LineStation.of(downStation, upStation, distance));
    }

    protected Line() {

    }

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(String name, String color) {
        return new Line(name, color);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
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

    public List<LineStation> getLineStationsInOrder() {
        return lineStations.getLineStationsInOrder();
    }

    public List<Long> getLineStationIdsInOrder() {
        return getLineStationsInOrder()
            .stream()
            .map(LineStation::getStationId)
            .collect(Collectors.toList());
    }
}
