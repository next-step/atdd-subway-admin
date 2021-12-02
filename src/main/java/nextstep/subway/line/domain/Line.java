package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private LineName name;

    @Embedded
    private LineColor color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = new LineName(name);
        this.color = new LineColor(color);
        sections.addToSections(this, upStation, downStation, distance);
    }

    public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(name, color, upStation, downStation, distance);
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        sections.addSection(this, upStation, downStation, distance);
    }

    public void update(Line line) {
        this.name = line.getLineName();
        this.color = line.getLineColor();
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public Long getId() {
        return id;
    }

    public LineName getLineName() {
        return name;
    }

    public LineColor getLineColor() {
        return color;
    }

    public String getName() {
        return name.getName();
    }

    public String getColor() {
        return color.getColor();
    }
}
