package nextstep.subway.line.domain;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;
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

    public Line(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(final String name, final String color, final long distance, final Station upStation, final Station downStation) {
        Line line = new Line(name, color);
        initSection(distance, upStation, downStation, line);
        return line;
    }

    private static void initSection(final long distance, final Station upStation, final Station downStation, final Line line) {
        Section section = Section.of(upStation, downStation, distance);
        line.addSection(section);
    }

    public void updateNameAndColor(final LineUpdateRequest lineUpdateRequest) {
        this.color = lineUpdateRequest.getColor();
        this.name = lineUpdateRequest.getName();
    }

    public void addSection(final Section section) {
        section.updateLine(this);
        sections.add(section);
    }

    public boolean containSection(final Section section) {
        return sections.contains(section);
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

    public Set<StationResponse> getAllStations() {
        return sections.getAllStation()
                .stream()
                .map(StationResponse::of)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", sections=" + sections +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color) && Objects.equals(sections, line.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, sections);
    }
}
