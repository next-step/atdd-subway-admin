package nextstep.subway.line.domain;

import static nextstep.subway.line.domain.exception.LineExceptionMessage.ALREADY_ADDED_SECTION;
import static nextstep.subway.line.domain.exception.LineExceptionMessage.ALREADY_ADDED_UP_DOWN_STATION;

import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.common.BaseEntity;
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
        validateAddableSection(section);
        validateAddableStation(section);
        this.sections.add(section);
        section.registerLine(this);
    }

    private void validateAddableStation(Section section) {
        if (this.sections.containUpDownStation(section)) {
            throw new IllegalStateException(ALREADY_ADDED_UP_DOWN_STATION.getMessage());
        }
    }

    private void validateAddableSection(Section section) {
        if (this.sections.contains(section)) {
            throw new IllegalStateException(ALREADY_ADDED_SECTION.getMessage());
        }
    }

    public Long getId() {
        return this.id;
    }

    public void update(Line updateLine) {
        this.name = updateLine.getName();
        this.color = updateLine.getColor();
    }

    public LineStations findSortedLineStations() {
        return this.sections.findSortedStations();
    }
}
