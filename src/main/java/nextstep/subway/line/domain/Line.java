package nextstep.subway.line.domain;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
        section.updateFinalUpStation(true);
        section.updateFinalDownStation(true);
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

    public Station getFinalUpStation() {
        return sections.finalUpStation();
    }

    public Station getFinalDownStation() {
        return sections.finalDownStation();
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
