package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Column(unique = true)
    private String name;

    private String color;

    //@OneToMany(mappedBy = "line", fetch = FetchType.LAZY)
    @Embedded
    private Sections sections = new Sections();

    private int distance;

    protected Line() {
    }

    public Line(String name, String color, List<Section> sections, int distance) {
        this.name = name;
        this.color = color;
        sections.stream()
                .forEach(section -> addSection(section));
        this.distance = distance;
    }

    private void addSection(Section section) {
        this.sections.addSection(section);
        section.setLine(this);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
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

    public int getDistance() {
        return distance;
    }

    public void updateLine(Line updateLine) {
        this.name = updateLine.getName();
        this.color = updateLine.getColor();
    }
}
