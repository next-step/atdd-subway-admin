package nextstep.subway.line.domain;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private Sections sections = Sections.of(new ArrayList<>());

    protected Line() {
    }

    private Line(final String name, final String color, final Sections sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    private Line(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(final String name, final String color, final Sections sections) {
        return new Line(name, color, sections);
    }

    public static Line of(final String name, final String color) {
        return new Line(name, color);
    }

    public void update(final Line line) {
        this.name = line.getName();
        this.color = line.getColor();
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

    public List<Section> getSections() {
        return sections.getSections();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public void addSection(Section section) {
        this.sections.add(section, this);
    }

    public List<Station> getStations() {
        return sections.getSections().stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<StationResponse> getStationResponses() {
        return getStations().stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public boolean isContainingStation(Station station){
        return getStations().contains(station);
    }
}
