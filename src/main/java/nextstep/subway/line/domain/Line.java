package nextstep.subway.line.domain;

import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.station.domain.Station;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    @Embedded
    private Sections sections;

    protected Line() {
    }

    public Line(String name, String color, Sections sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void merge(final Line otherLine) {
        this.name = otherLine.name;
        this.color = otherLine.color;
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public void removeStation(Station station) {
        sections.removeStation(station);
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

    public List<Station> stations() {
        return sections.stations();
    }

    public Sections getSections() {
        return sections;
    }
}
