package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "line")
    private final List<Section> sections = new ArrayList<>();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        sections.add(new Section(this, upStation, downStation, new Distance(distance)));
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public List<StationResponse> getStationResponses() {
        Set<StationResponse> stations = new LinkedHashSet<>();
        sections.forEach(section -> {
            stations.add(StationResponse.of(section.getUpStation()));
            stations.add(StationResponse.of(section.getDownStation()));
        });
        return new ArrayList<>(stations);
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
}
