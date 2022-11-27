package nextstep.subway.line;

import nextstep.subway.section.Section;
import nextstep.subway.section.Sections;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String color;

    @Embedded
    private final Sections sections = new Sections();

    protected Line() { }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void addSection(Section section) {
        section.toLine(this);
        sections.add(section);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Map<String, Object>> getStations() {
        return sections.getStationsResponse();
    }

    public void update(Line updateLine) {
        this.name = updateLine.name;
        this.color = updateLine.color;
    }
}
