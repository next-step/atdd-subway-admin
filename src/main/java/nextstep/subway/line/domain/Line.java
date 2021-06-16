package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Table(name = "line")
@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Sections sections = new Sections();

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "color", nullable = false)
    private String color;

    protected Line() {
    }

    public Line(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    private Line(final Section section, final String name, final String color) {
        addSection(section);
        this.name = name;
        this.color = color;
    }

    public static Line of(final Station upStation, final Station downStation, final String name, final String color,
                          final int distance) {

        final Section section = new Section(upStation, downStation, distance);
        return new Line(section, name, color);
    }

    public void update(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(final Station upStation, final Station downStation, final int distance) {
        final Section section = new Section(upStation, downStation, distance);
        addSection(section);
    }

    private void addSection(final Section section) {
        this.sections.registerNewSection(section);
        section.setLine(this);
    }

    public void removeSectionByStation(final Station targetStation) {
        this.sections.removeSectionByStation(targetStation);
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

    public List<Station> getSortedStations() {
        return new ArrayList<>(sections.getSortedStations());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
