package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    @Embedded
    private Color color;

    protected Line() {
    }

    @Embedded
    private Sections sections = new Sections();

    public Line(String name, String color) {
        this.name = new Name(name);
        this.color = new Color(color);
    }

    public Line(String name, String color, Section section) {
        this.name = new Name(name);
        this.color = new Color(color);
        addSection(section);
    }

    public void addSection(Section section) {
        section.setLine(this);
        this.sections.addSection(section);
    }

    public void update(Line line) {
        this.name = new Name(line.getName());
        this.color = new Color(line.getColor());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getName();
    }

    public String getColor() {
        return color.getColor();
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void deleteStation(Long stationId) {
        sections.deleteSection(stationId);
    }
}
