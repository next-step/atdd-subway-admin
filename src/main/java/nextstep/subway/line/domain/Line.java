package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;

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

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.addSection(section);
    }

    private void addSection(Section section) {
        this.sections.addSection(section);
        section.setLine(this);
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

    public Sections getSections() {
        return sections;
    }

    public List<Section> getSectionsList(){
        return sections.getSection();
    }

    public void updateLine(Line updateLine) {
        this.name = updateLine.getName();
        this.color = updateLine.getColor();
        this.sections.getSection().clear();
        updateLine.getSections().getSection()
                .forEach(station -> this.addSection(station));
    }
}
