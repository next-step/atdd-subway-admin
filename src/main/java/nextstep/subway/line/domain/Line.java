package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(Line line) {
        this.name = line.name;
        this.color = line.color;
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public Long getId() {
        return id;
    }

    public Sections createSections() {
        return new Sections(this.sections);
    }

    public void removeSection(Section removedSection) {
        this.sections.remove(removedSection);
    }

    public LineResponse toLineResponse() {
        return new LineResponse(this.id, this.name, this.color, getCreatedDate(), getModifiedDate(),
                new Sections(sections).getSortedStations()
                .stream()
                .map(Station::toStationResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) &&
                Objects.equals(name, line.name) &&
                Objects.equals(color, line.color) &&
                Objects.equals(sections, line.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, sections);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", sections=" + sections.toString() +
                '}';
    }
}
