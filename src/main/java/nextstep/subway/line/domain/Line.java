package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;
    private Sections sections = new Sections();


    public Line() {
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        addSection(section);
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

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        if (!sections.isContainsSection(section)) {
            sections.add(section);
        }
        if (section.getLine() == null || !section.getLine().equals(this)) {
            section.setLine(this);
        }
    }

    public void removeSection(Section section) {
        sections.removeSection(section);
        if (this.equals(section.getLine())) {
            section.removeLine(this);
        }
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public boolean isContainsSection(Section section) {
        return sections.isContainsSection(section);
    }
}
