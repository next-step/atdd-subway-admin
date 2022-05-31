package nextstep.subway.line.domain;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;

import javax.persistence.*;
import java.util.Objects;

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
        section.assignLine(this);
    }

    public void update(Line line) {
        this.name = line.name;
        this.color = line.color;
    }

    public LineResponse toLineResponse() {
        return LineResponse.of(this, this.sections.allStationResponses());
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
