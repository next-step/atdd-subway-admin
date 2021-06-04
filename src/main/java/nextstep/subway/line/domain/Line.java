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

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.REMOVE)
    private List<Section> sections = new ArrayList<>();

    public Line() { }

    public Line(String name, String color, List<Section> sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        if (section != null) {
            this.sections.add(section);
            section.setLine(this);
        }
    }

    public Line(String name, String color) {
        this(name, color, (Section) null);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
        this.sections = line.getSections();
    }

    public List<Section> getSections() {
        return this.sections;
    }

    public List<Station> getStations() {
        Set<Station> stations = sections.stream()
                .flatMap(station -> Stream.of(station.getUpStation(), station.getDownStation()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return stations.stream().collect(Collectors.toList());
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

    public void addSection(Section section) {
        if (!this.sections.contains(section)) {
            this.sections.add(section);
        }
    }
}
