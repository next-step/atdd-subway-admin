package nextstep.subway.domain;

import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, unique = true)
    private String color;
    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, Long distance) {
        this.name = name;
        this.color = color;
        addSection(new Section(this, upStation, downStation, new Distance(distance)));
    }

    public Line(String name, String color, Sections sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public static Line of(LineRequest lineRequest, Station upStation, Station downStation) {
        return new Line(lineRequest.getName(), lineRequest.getColor(), upStation, downStation, lineRequest.getDistance());
    }

    public static Line of(LineUpdateRequest lineUpdateRequest, Sections sections) {
        return new Line(lineUpdateRequest.getName(), lineUpdateRequest.getColor(), sections);
    }

    public void addSection(Section newSection) {
        this.sections.add(newSection);
    }

    public void update(Line newLine) {
        this.name = newLine.name;
        this.color = newLine.color;
    }

    public boolean includeAnyStation(Station newUpStation, Station newDownStation) {
        List<Station> stations = this.sections.findStations();
        return stations.stream().anyMatch(station -> station.equals(newUpStation) || station.equals(newDownStation));
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

    public void removeSection(Station station) {
        this.sections.remove(station);
    }
}
