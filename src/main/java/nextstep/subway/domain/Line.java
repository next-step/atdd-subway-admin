package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "line")
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "color")
    private String color;

    @Embedded
    private LineStations lineStations = new LineStations();

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public void setFinalStations(final Station finalUpStation, final Station finalDownStation, final Long distance) {
        relateToStation(new LineStation(this, finalUpStation, null));
        relateToStation(new LineStation(this, finalDownStation, finalUpStation));
        sections.add(new Section(this, finalUpStation, finalDownStation, distance));
    }

    public void update(final String newName, final String newColor) {
        name = newName;
        color = newColor;
    }

    public void relateToSection(final Station upStation, final Station downStation, final Long distance) {
        final Station previous = lineStations.getPreviousOf(upStation);
        relateToStation(new LineStation(this, upStation, previous));
        relateToStation(new LineStation(this, downStation, upStation));
        sections.add(new Section(this, upStation, downStation, distance));
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

    public LineStations getLineStations() {
        return lineStations;
    }

    public Sections getSections() {
        return sections;
    }

    private void relateToStation(final LineStation lineStation) {
        lineStations.add(lineStation);
    }
}
