package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.OrderedSections;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
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

    @Embedded
    private Sections sections = new Sections();

    public Line() { }

    private Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.addSection(section);
        section.setLine(this);
    }

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(String name, String color, Section section) {
        return new Line(name, color, section);
    }

    public static Line of(String name, String color) {
        return new Line(name, color);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public Sections getSections() {
        return this.sections;
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(station -> Stream.of(station.getUpStation(), station.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Station> getOrderedStations() {
        return sections.orderedStations();
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

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public void addSection(Section sectionIn) {
        if (sections.isEmpty()) {
            sections.add(sectionIn);
            return;
        }

        sections.validateAbout(sectionIn);

        checkConnectingPositionThroughOrderedSections(sectionIn);

        sectionIn.setLine(this);
    }

    private void checkConnectingPositionThroughOrderedSections(Section sectionIn) {
        OrderedSections orderedSections = sections.orderedSections();
        orderedSections.connectThroughOrderedSections(sectionIn);
        sections.add(sectionIn);
    }

    public void delete(Station station) {
        sections.delete(station);
    }
}
