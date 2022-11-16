package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {

    @Id
    @Column(name = "line_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column
    private String color;
    @Embedded
    private Sections sections;

    protected Line() {
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        section.setLine(this);
        sections = new Sections(section);
    }

    public static Line of(final String name, final String color, final Section section) {
        section.registerEndPoint();
        return new Line(name, color, section);
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

    public void changeName(final String name) {
        this.name = name;
    }

    public void changeColor(final String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void addSection(Section section) {
        section.setLine(this);
        sections.add(section);
    }

    public void deleteSectionByStationId(final Long stationId) {
        sections.deleteByStationId(stationId);
    }
}
