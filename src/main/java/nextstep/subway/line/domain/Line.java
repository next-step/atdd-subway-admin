package nextstep.subway.line.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.LineSections;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SortedSection;
import nextstep.subway.station.domain.SortedStations;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -1326863547152158454L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private LineSections lineSections;

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.lineSections = new LineSections();
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public void addSection(Section section) {
        lineSections.add(section);
        section.toLine(this);
    }

    public void updateSections(LineSections lineSections) {
        this.lineSections = lineSections;
        lineSections.updateLine(this);
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

    public LineSections getSections() {
        return lineSections;
    }

    public SortedStations getSortedStations() {
        return lineSections.toStations();
    }

    public SortedSection getSortedSections() {
        return lineSections.toSortedSections();
    }

    public void deleteSection(Station station) {
        lineSections.deleteSection(station);
    }
}
