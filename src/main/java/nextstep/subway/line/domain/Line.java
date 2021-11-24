package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private Sections sections;

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;

        sections = Sections.valueOf();
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return this.color;
    }

    public Sections getSections() {
        return this.sections;
    }

    public List<Station> getStations() {
        if (this.sections.isEmpty()) {
            return new ArrayList<>();
        }

        List<Station> stations = this.sections.findUpStations();

        Section lastSection = this.sections.findLastItem();

        stations.add(lastSection.getDownStation());

        return stations;
    }

    public void deleteStation(Station deletingStation) {
        this.sections.deleteSection(deletingStation);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Line)) {
            return false;
        }
        Line line = (Line) o;
        return  Objects.equals(name, line.name) && Objects.equals(color, line.color) && Objects.equals(sections, line.sections);
       
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
