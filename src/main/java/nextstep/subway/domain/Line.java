package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(nullable = false)
    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name) {
        this.name = name;
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, Integer distance) {
        this.name = name;
        this.color = color;
        sections.addSection(upStation, downStation, distance);
    }

    public void addSection(Section section) {
        sections.addSection(section);
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

    public List<Section> getSections() {
        return sections.getSections();
    }
}
