package nextstep.subway.line.domain;

import static java.util.stream.Collectors.toList;

import com.google.common.collect.Lists;
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

    protected Line() {
    }

    private Line(String name, String color) {
        this.name = LineName.from(name);
        this.color = LineColor.from(color);
    }

    private Line(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = LineName.from(name);
        this.color = LineColor.from(color);
        this.sections = new Sections(
            Lists.newArrayList(Section.of(this, upStationId, downStationId, distance)));
    }

    public static Line from(String name, String color) {
        return new Line(name, color);
    }

    public static Line from(String name, String color, Long upStationId, Long downStationId, int distance) {
        return new Line(name, color, upStationId, downStationId, distance);
    }

    public List<Long> getStationIds() {
        return sections.extractStationsWithOrdering()
            .stream()
            .map(Station::getId)
            .collect(toList());
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public void addSection(Section section) {
        sections.connect(section);
    }

    public Long getId() {
        return id;
    }

    public LineName getName() {
        return name;
    }

    public LineColor getColor() {
        return color;
    }

    public Sections getSections() {
        return sections;
    }
}
