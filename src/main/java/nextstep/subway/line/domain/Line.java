package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Stations;

@Entity
public class Line extends BaseEntity {
    @Column(unique = true)
    private String name;
    private String color;
    private Sections sections;

    protected Line() {
    }

    public Line(final String name, final String color) {
        this.name = name;
        this.color = color;
        this.sections = new Sections();
    }

    public void withSection(final Section section) {
        this.sections.add(section);
    }

    public void update(final Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return this.color;
    }

    public Stations toStations() {
        return this.sections.toStations();
    }
}
