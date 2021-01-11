package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;
    @Embedded
    private final Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this(name, color);
        addSection(SectionOf(upStation, downStation, distance));
    }

    private Section SectionOf(Station upStation, Station downStation, int distance) {
        return Section.builder()
                .line(this)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance).build();
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public void addSection(Section section) {
        sections.add(section);
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

    public List<StationResponse> getStationResponses() {
        return sections.getStations().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }
}
