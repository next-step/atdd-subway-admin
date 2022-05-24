package nextstep.subway.line.domain;

import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.constants.LineExceptionMessage;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

@Entity
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

    public static Line of(String name, String color) {
        return new Line(LineName.from(name), LineColor.from(color));
    }

    public LineName getName() {
        return this.name;
    }

    public LineColor getColor() {
        return this.color;
    }

    public void addSection(Section section) {
        validateSections(section);
        this.sections.add(section);
        section.registerLine(this);
    }

    private void validateSections(Section section) {
        if (this.sections.contains(section)) {
            throw new IllegalStateException(LineExceptionMessage.ALREADY_ADDED_SECTION);
        }
    }

    public Long getId() {
        return this.id;
    }

    public List<Station> getStations() {
        return this.sections.getStations();
    }
}
