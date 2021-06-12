package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.wrappers.Sections;
import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.lineStation.domain.wrappers.LineStations;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
public class Line extends BaseEntity {
    private static final String NOT_FOUND_LINE_ERROR_MESSAGE = "요청한 id 기준 노선이 존재하지않습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    @Embedded
    private LineStations lineStations = new LineStations();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Sections sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(String name, String color, LineStations lineStations) {
        this.name = name;
        this.color = color;
        this.lineStations = lineStations;
    }

    public Line(String name, String color, Section section, LineStations lineStations) {
        this.name = name;
        this.color = color;
        this.sections = new Sections(Arrays.asList(section));
        this.lineStations = lineStations;
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public static Line getNotNullLine(Optional<Line> optionalLine) {
        if (!optionalLine.isPresent()) {
            throw new IllegalArgumentException(NOT_FOUND_LINE_ERROR_MESSAGE);
        }
        return optionalLine.get();
    }

    public List<Station> stations() {
        return sections.generateStations();
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

    public void addSection(Section section) {
        sections.addSection(section);
        section.lineBy(this);
    }

    public void addLineStation(LineStation lineStation) {
        lineStations.addLineStation(lineStation);
        lineStation.lineBy(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) &&
                Objects.equals(name, line.name) &&
                Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
