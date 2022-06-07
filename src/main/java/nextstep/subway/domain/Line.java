package nextstep.subway.domain;

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

    private String name;

    private String color;

    @Embedded
    private final Sections sections = new Sections();

    protected Line() {
    }

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line createLine(String name, String color, Station upStation, Station downStation, int distance) {
        Line line = new Line(name, color);
        line.addSection(new Section(upStation, downStation, distance));

        return line;
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

    public void updateNameColor(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section newSection) {
        newSection.setLine(this);
        sections.add(newSection);
    }

    public List<Station> getStationsInOrder() {
        return sections.getStationsInOrder();
    }
}
