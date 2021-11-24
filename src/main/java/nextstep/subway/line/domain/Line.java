package nextstep.subway.line.domain;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
@Table(name = "line")
public class Line extends BaseEntity {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "color")
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public void addSection(Section section) {
        sections.add(section);
        section.setLine(this);
    }

    protected Line() {

    }

    private Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;

        addSection(section);
    }

    public static Line of(String name, String color, Section section) {
        return new Line(name, color, section);
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

    public Sections getSections() {
        return sections;
    }

    public List<Long> getStationIdsInOrder() {
        return sections.getStationsInOrder()
            .getValues()
            .stream()
            .map(Station::getId)
            .collect(Collectors.toList());
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void removeSectionByStation(Station station) {
        sections.removeByStation(station);
    }
}
