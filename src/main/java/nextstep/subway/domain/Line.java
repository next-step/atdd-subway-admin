package nextstep.subway.domain;

import java.util.Collections;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    @Embedded
    private Color color;

    @Embedded
    private Sections sections;

    protected Line() {
    }

    private Line(String name, String color, int distance, Station upStation, Station downStation) {
        Section section = Section.of(upStation, downStation, this, distance);
        this.name = Name.from(name);
        this.color = Color.from(color);
        this.sections = Sections.from(Collections.singletonList(section));
    }

    public static Line of(String name, String color, int distance, Station upStation, Station downStation) {
        return new Line(name, color, distance, upStation, downStation);
    }

    public void modify(String name, String color) {
        this.name = Name.from(name);
        this.color = Color.from(color);
    }

    public void addSection(Section section) {
        sections.add(section);
        section.setLine(this);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.value();
    }

    public String getColor() {
        return color.value();
    }

    public List<Station> getStations() {
        return sections.getStations();
    }
}
