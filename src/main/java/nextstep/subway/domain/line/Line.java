package nextstep.subway.domain.line;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.domain.BaseEntity;
import nextstep.subway.domain.station.Station;
import nextstep.subway.dto.UpdateLine;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String color;

    @Embedded
    private LineStations lineStations = new LineStations();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addLineStation(LineStation lineStation) {
        if (!lineStations.isEmpty()) {
            lineStation.validate(lineStations);
        }
        this.lineStations.add(lineStation);
        lineStation.addLine(this);
    }

    public void addLineStationWithoutSettingLine(LineStation lineStation) {
        lineStations.add(lineStation);
    }

    public boolean containLineStation(LineStation lineStation) {
        return lineStations.contains(lineStation);
    }

    public void deleteLineStation(Station station) {
        lineStations.deleteLineStation(station);
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

    public void update(UpdateLine request) {
        this.name = request.getName();
        this.color = request.getColor();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name)
                && Objects.equals(color, line.color) && Objects.equals(lineStations, line.lineStations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, lineStations);
    }
}
