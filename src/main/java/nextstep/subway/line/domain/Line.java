package nextstep.subway.line.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private LineSections lineSections = new LineSections();

    public Line() {
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
        this.lineSections = this.getLineSections();
    }

    public void addLineSection(Section section) {
        this.lineSections.add(section);
        section.changeLine(this);
    }

    public List<Station> toLineStations() {
        return lineSections.toStations();
    }

    public void removeSectionByStation(Station station) {
        lineSections.removeSectionByStation(station);
    }

    public List<Section> getOrderedLineSections() {
        return lineSections.getOrderedLineSections();
    }

    public List<Station> getOrderedStation() {
        return lineSections.getOrderedStation();
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

    public LineSections getLineSections() {
        return lineSections;
    }
}
