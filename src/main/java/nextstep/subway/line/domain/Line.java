package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @OneToMany(mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public void addSection(Section section) {
        sections.add(section);
        section.addLine(this);
    }

    public List<Station> getEachEndStations() {
        return getUpDirectionEndStation().flatMap(upStation ->
            getDownDirectionEndStation().map(downStation ->
                Stream.of(upStation, downStation)
                    .collect(Collectors.toList())))
            .orElseGet(Collections::emptyList);
    }

    private Optional<Station> getUpDirectionEndStation() {
        return sections.stream()
            .findFirst()
            .map(Section::getUpStation);
    }

    private Optional<Station> getDownDirectionEndStation() {
        return getReverseOrderedSections().stream()
            .findFirst()
            .map(Section::getDownStation);
    }

    private List<Section> getReverseOrderedSections() {
        List<Section> reversedSections = new ArrayList<>(sections);
        Collections.reverse(reversedSections);
        return reversedSections;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return id.equals(line.id) && name.equals(line.name) && color.equals(line.color) && sections.equals(line.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, sections);
    }
}
