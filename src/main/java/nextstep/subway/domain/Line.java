package nextstep.subway.domain;

import com.sun.istack.NotNull;
import nextstep.subway.consts.ErrorMessage;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String color;

    @Embedded
    private Sections sections;

    protected Line() {}

    private Line( String name, String color, int distance, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;
        this.sections = new Sections(new Section(upStation, downStation, distance, this));
    }

    public static Line of(String name ,String color, int distance, Station upStation, Station downStation) {
        return new Line(name, color, distance, upStation, downStation);
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
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

    public void update(Line newLine) {
        this.name = newLine.getName();
        this.color = newLine.getColor();
    }

    public void addSection(Section section) {
        validateSection(section);
        sections.add(section);
        section.setLine(this);
    }

    private void validateSection(Section section) {
        if (section.isExistSections(sections.getStations())) {
            throw new IllegalArgumentException(ErrorMessage.ERROR_STATIONS_ALREADY_EXIST);
        }
        if (!section.isIncludeStation(sections.getStations())) {
            throw new IllegalArgumentException(ErrorMessage.ERROR_STATIONS_NOT_ALL);
        }
    }

    public void removeStation(Station station) {
        sections.removeStation(station);
    }
}
