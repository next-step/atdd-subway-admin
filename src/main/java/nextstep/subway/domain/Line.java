package nextstep.subway.domain;

import nextstep.subway.dto.LineRequest;

import javax.persistence.*;
import java.util.Objects;

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
    private final LineStations lineStations = new LineStations();

    protected Line() {
    }

    public Line(final String name, final String color) {
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

    public void updateName(String name) {
        if (!name.isEmpty() && !Objects.equals(this.name, name)) {
            this.name = name;
        }
    }

    public void updateColor(String color) {
        if (Objects.nonNull(color) && !color.isEmpty() && !Objects.equals(this.color, color)) {
            this.color = color;
        }
    }

    public void updateBy(LineRequest request) {
        this.updateName(request.getName());
        this.updateColor(request.getColor());
    }

    public void addLineStation(final LineStation lineStation) {
        if (!this.lineStations.isContains(lineStation)) {
            this.lineStations.addLineStation(lineStation);
        }
    }

    public LineStations getLineStations() {
        return lineStations;
    }

    public void removeLineStation(final LineStation removeLineStation) {
        if (Objects.equals(removeLineStation.getLine(), this)) {
            removeLineStation.updateLineBy(null);
            this.lineStations.removeLineStationBy(removeLineStation);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
