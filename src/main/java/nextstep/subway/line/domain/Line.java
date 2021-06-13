package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.wrappers.Sections;
import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.lineStation.domain.wrappers.LineStations;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.wrapper.Distance;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
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

    public Line(String name, String color, Sections sections, LineStations lineStations) {
        this.name = name;
        this.color = color;
        this.sections = sections;
        this.lineStations = lineStations;
    }

    public Line lineStationsBy(LineStations lineStations) {
        this.lineStations = lineStations;
        lineStations.addLine(this);
        return this;
    }

    public Line addSection(Section section) {
        sections.addSection(section);
        section.lineBy(this);
        return this;
    }

    public void addLineStation(LineStation lineStation) {
        lineStations.addLineStation(lineStation);
        lineStation.lineBy(this);
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

    public Section createNewSection(LineStation lineStation) {
        List<LineStation> lineStations = this.lineStations.getLineStationsOrderByAsc();
        if (lineStations.get(0).getStation().getId() == lineStation.getStation().getId()) {
            lineStations.get(0).update(lineStation.getStation(), lineStation.getPreStation(), lineStation.getDistance());
            Section section = new Section(this, lineStation.getPreStation(), lineStation.getStation(), lineStation.getDistance());
            return section;
        }
        if (lineStations.get(lineStations.size() - 1).getStation().getId() == lineStation.getPreStation().getId()) {
            Section section = new Section(this, lineStation.getPreStation(), lineStation.getStation(), lineStation.getDistance());
            return section;
        }

        Optional<LineStation> first = lineStations
                .stream()
                .filter(ls -> ls.isSamePreStation(lineStation))
                .findFirst();

        if (first.isPresent()) {
            LineStation targetLineStation = first.get();
            Distance newDistance = targetLineStation.getDistance().subtractionDistance(lineStation.getDistance());
            targetLineStation.update(targetLineStation.getStation(), lineStation.getStation(), newDistance);
            this.sections.updateSectionByDownStation(targetLineStation, newDistance);
        }
        return new Section(this, lineStation.getPreStation(), lineStation.getStation(), lineStation.getDistance());
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
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Line line = (Line) object;
        return Objects.equals(id, line.id) &&
                Objects.equals(name, line.name) &&
                Objects.equals(color, line.color) &&
                Objects.equals(sections, line.sections) &&
                Objects.equals(lineStations, line.lineStations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, sections, lineStations);
    }
}
