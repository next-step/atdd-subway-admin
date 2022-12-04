package nextstep.subway.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.StringUtils;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column
    private String color;
    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    private Line(String name, String color) {
        validNameAndColor(name, color);

        this.name = name;
        this.color = color;
    }

    public static Line of(String name, String color) {
        return new Line(name, color);
    }

    public static Line of(String name, String color, Section section) {
        Line line = new Line(name, color);
        line.addSection(section);
        return line;
    }

    private void validNameAndColor(String name, String color) {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(color)) {
            throw new IllegalArgumentException();
        }
    }

    public void updateLine(Line line) {
        this.name = line.name;
        this.color = line.color;
    }

    public void addSection(Section section) {
        sections.addSection(section);
        section.toLine(this);
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

    public List<Station> getStations() {
        return sections.getStations();
    }

    public int totalDistance() {
        return sections.totalDistance();
    }
}