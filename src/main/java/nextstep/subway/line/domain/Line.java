package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;
    @OneToMany(mappedBy = "line", cascade = CascadeType.REMOVE)
    private List<Section> sections;

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.sections = new ArrayList<>();
    }

    public Line update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
        return this;
    }

    public void addSection(Section section) {
        section.ofLine(this);
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

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(sections.stream()
                .findFirst()
                .map(section -> section.getUpStation())
                .get());

        stations.addAll(sections.stream()
                .map(section -> section.getDownStation())
                .collect(Collectors.toList()));

        return stations;
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
