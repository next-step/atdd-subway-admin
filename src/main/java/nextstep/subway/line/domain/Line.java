package nextstep.subway.line.domain;

import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private LineSections lineSections = new LineSections();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void add(Section section) {
        this.lineSections.addTerminal(this, section);
    }

    public List<Station> getStationsFromUpTerminal() {
        return lineSections.getStationsFromUpTerminal();
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Line))
            return false;
        Line line = (Line)o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name)
            && Objects.equals(color, line.color) && Objects.equals(lineSections, line.lineSections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, lineSections);
    }
}
