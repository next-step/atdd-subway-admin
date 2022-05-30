package nextstep.subway.line.domain;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "line")
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private LineName name;

    @Embedded
    private LineColor color;

    @Embedded
    private Sections sections = Sections.empty();

    protected Line() {}

    public Line(LineName name, LineColor color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this(LineName.from(name), LineColor.from(color));
    }

    public void addSection(Section section) {
        this.sections.add(section);
        section.toLine(this);
    }

    public void update(Line line) {
        this.name = line.name;
        this.color = line.color;
    }

    public LineResponse toLineResponse() {
        return LineResponse.of(this, allStationResponses());
    }

    private List<StationResponse> allStationResponses() {
        return allStations().stream()
                .map(Station::toStationResponse)
                .collect(Collectors.toList());
    }

    private List<Station> allStations() {
        List<Station> allStations = new ArrayList<>();
        getSections().forEach(section -> {
            allStations.addAll(section.getStations());
        });
        return allStations.stream().distinct().collect(Collectors.toList());
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name.get();
    }

    public String getColor() {
        return this.color.get();
    }

    public List<Section> getSections() {
        return this.sections.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
