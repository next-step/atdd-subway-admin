package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.enums.SectionAddType;
import nextstep.subway.wrappers.Distance;
import nextstep.subway.wrappers.Sections;
import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.wrappers.LineStations;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
public class Line extends BaseEntity {
    private static final String NOT_FOUND_LINE_ERROR_MESSAGE = "요청한 id 기준 노선이 존재하지않습니다.";
    public static final String DUPLICATE_LINE_STATION_ERROR_MESSAGE = "상행역 %s 하행역 %s은 이미 등록된 구간 입니다.";
    public static final String NOT_CONTAION_STATIONS_ERROR_MESSAGE = "상행역 %s, 하행역 %s을 둘중 하나라도 포함하는 구간이 존재하지않습니다.";

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

    public List<LineStation> getLineStationsOrderByAse() {
        return this.lineStations.getLineStationsOrderByAsc();
    }

    public void updateLineStationAndSection(SectionAddType sectionAddType, LineStation lineStation) {
        if (sectionAddType.equals(SectionAddType.NEW_UP)) {
            lineStations.updateFirstLineStation(lineStation);
        }
        if (sectionAddType.equals(SectionAddType.NEW_BETWEEN)) {
            LineStation updateTargetLineStation = lineStations.findLineStationByPreStation(lineStation.getPreStation());
            updateTargetLineStation.validDistance(lineStation);
            Distance newDistance = new Distance(updateTargetLineStation.getDistance().subtractionDistance(lineStation.getDistance()));
            updateTargetLineStation.update(updateTargetLineStation.getStation(), lineStation.getStation(), newDistance);
            sections.updateSectionByDownStation(updateTargetLineStation, newDistance);
        }
    }

    public void checkValidLineStation(LineStation lineStation) {
        checkValidDuplicateLineStation(lineStation);
        checkValidNotContainStations(lineStation);
    }

    private void checkValidDuplicateLineStation(LineStation lineStation) {
        if (lineStations.isSameLineStation(lineStation)) {
            throw new IllegalArgumentException(
                    String.format(DUPLICATE_LINE_STATION_ERROR_MESSAGE, lineStation.getPreStation().getName(), lineStation.getStation().getName())
            );
        }
    }

    private void checkValidNotContainStations(LineStation lineStation) {
        if (lineStations.isNotContainStations(lineStation)) {
            throw new IllegalArgumentException(
                    String.format(NOT_CONTAION_STATIONS_ERROR_MESSAGE, lineStation.getPreStation().getName(), lineStation.getStation().getName()));
        }
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
