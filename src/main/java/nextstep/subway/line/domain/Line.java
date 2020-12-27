package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;

import javax.persistence.Column;
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
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private final Sections sections = new Sections();

    public Line() {
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

    public void updateSection(Section section) {
        this.sections.update(section);
    }

    public void addSection(Section targetSection) {
        targetSection.setLine(this);
        this.sections.add(targetSection);
    }

    public void createSection(Section targetSection) {
        targetSection.setLine(this);
        this.sections.create(targetSection);
    }

    public List<Section> getSections() {
        return this.sections.getSections();
    }
}
