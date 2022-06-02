package nextstep.subway.domain;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Where(clause = "deleted=false")
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @Embedded
    private final Sections sections = new Sections();

    private Boolean deleted = false;

    protected Line() {

    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(String name, String color, Section section) {
        Line line = new Line(name, color);
        line.addSection(section);
        return line;
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void delete() {
        this.deleted = true;
    }

    public boolean isDelete() {
        return this.deleted;
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void addSection(Section section) {
        section.setLine(this);
        sections.addSections(section);
    }
}
