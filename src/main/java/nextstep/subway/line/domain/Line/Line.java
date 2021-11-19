package nextstep.subway.line.domain.Line;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.LineStation.LineStation;
import nextstep.subway.line.domain.LineStation.LineStations;

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
    private final LineStations stations = new LineStations();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
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
        return stations;
    }

    public void addLineStation(LineStation lineStation) {
        this.stations.addLineStation(lineStation);
    }

    public void deleteLineByLineStation(Long lineStationsId) {
        stations.delete(lineStationsId);
    }
}
