package nextstep.subway.domain.line.domain;

import nextstep.subway.domain.common.BaseEntity;
import nextstep.subway.domain.section.domain.Section;
import nextstep.subway.domain.station.domain.Station;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "color")
    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
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

    public void addSection(final Section section) {
        this.sections.addSection(section);
    }

    public void createSection(final Section section) {
        this.sections.createSection(section);
    }

    public List<Section> getSections() {
        return sections.getStationInOrder();
    }

    public void removeSection(final Station station) {
        this.sections.remove(station);
    }
}
