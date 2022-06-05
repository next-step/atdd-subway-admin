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

    @Transient
    @Embedded
    private final Section section = new Section();

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

    public Station getUpStation() {
        return section.getUpStation();
    }

    public Station getDownStation() {
        return section.getDownStation();
    }

    public Distance getDistance() {
        return section.getDistance();
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

    public Line upStationBy(final Station upStation) {
        this.section.updateUpStationBy(upStation);
        return this;
    }

    public void updateBy(LineRequest request) {
        this.updateName(request.getName());
        this.updateColor(request.getColor());
    }

    public Line downStationBy(Station downStation) {
        this.section.updateDownStationBy(downStation);
        return this;
    }


    public void addLineStation(final LineStation lineStation) {
        if (!this.lineStations.isContains(lineStation)) {
            this.lineStations.addLineStation(lineStation);
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
