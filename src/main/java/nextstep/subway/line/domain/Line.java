package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    private Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        addSection(Section.of(upStation, downStation, distance));
    }

    private Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        addSection(section);
    }

    public static Line of(String name, String color) {
        return new Line(name, color);
    }

    public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(name, color, upStation, downStation, distance);
    }

    public static Line of(String name, String color, Section section) {
        return new Line(name, color, section);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public void addSection(Section newSection) {
        sections.add(newSection);
        newSection.setLine(this);
    }

    public void addSections(List<Section> newSections) {
        newSections.forEach(this::addSection);
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

    public List<Section> getSectionsInOrder() {
        return Collections.unmodifiableList(sections.getSectionsInOrder());
    }

    public List<Station> getStationInOrder() {
        List<Station> stations = new LinkedList<>();
        List<Section> orderedSections = sections.getSectionsInOrder();
        Section firstSection = orderedSections.get(0);

        stations.add(firstSection.getUpStation());
        stations.addAll(orderedSections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList()));

        return Collections.unmodifiableList(stations);
    }
}
