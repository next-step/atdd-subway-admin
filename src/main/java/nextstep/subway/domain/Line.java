package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private Sections sections = new Sections(new ArrayList<>());

    protected Line() {

    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
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

    public List<Station> getStations() {
        return sections.getStations();
    }

    public Sections getSections() {
        return sections;
    }

    public void updateNameAndColor(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public List<Section> addAndGetSections(Station requestUpStation, Station requestDownStation, Distance distance) {
        List<Section> sections = this.sections.addAndGetSections(requestUpStation, requestDownStation, distance);
        sections.forEach(section -> section.setLine(this));
        return sections;
    }

    public void initSection(Section section) {
        sections.init(section).stream().forEach(newSection -> newSection.setLine(this));
    }

    public void removeStation(Long stationId) {
        Section newSection = sections.removeSectionByStationAndGetNewSection(stationId);
        newSection.setLine(this);
    }
}
