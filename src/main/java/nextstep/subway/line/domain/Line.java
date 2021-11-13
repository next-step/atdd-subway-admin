package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public Line(String name, String color, List<Section> sections) {
        this(name, color);
        this.sections = sections;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this(name, color);
        Section upStationSection = new Section(this, upStation, 0);
        Section downStationSection = new Section(this, downStation, distance);
        this.sections = Arrays.asList(upStationSection, downStationSection);

    }

    public Line(Long id, String name, String color) {
        this(name, color);
        this.id = id;
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

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        if (!sections.contains(section)) {
            sections.add(section);
        }
    }

    public List<Station> getStations(){
        return this.sections.stream()
                .map(section -> section.getStation())
                .collect(Collectors.toList());
    }
}
