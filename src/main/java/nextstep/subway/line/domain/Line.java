package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private LineName name;

    @Embedded
    private LineColor color;

    @Embedded
    private Sections sections;

    protected Line(){
    }

    private Line(String name, String color, Section section) {
        this.name = LineName.from(name);
        this.color = LineColor.from(color);
        this.sections = Sections.from(section);
        section.updateLine(this);
    }

    public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(name, color, Section.of(upStation, downStation, distance));
    }

    public void update(String name, String color) {
        updateName(name);
        updateColor(color);
    }

    public Long getId() {
        return id;
    }

    public LineName getName() {
        return name;
    }

    public LineColor getColor() {
        return color;
    }

    public Sections getSections() {
        return sections;
    }

    private void updateName(String name) {
        this.name = LineName.from(name);
    }

    private void updateColor(String color) {
        this.color = LineColor.from(color);
    }

    public void addSection(Section section) {
        section.updateLine(this);
        sections.addSection(section);
    }
}
