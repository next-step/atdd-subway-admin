package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.enums.SectionAddType;
import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.wrappers.LineStations;
import nextstep.subway.wrappers.Sections;

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
    private LineStations lineStations = new LineStations();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, LineStations lineStations) {
        this.name = name;
        this.color = color;
        this.lineStations = lineStations;
    }

    public SectionAddType calcAddType(LineStation lineStation) {
        return SectionAddType.calcAddType(lineStations, lineStation);
    }

    public Line lineStationsBy(LineStations lineStations) {
        this.lineStations = lineStations;
        lineStations.addLine(this);
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
        return lineStations.generateStations();
    }

    public Optional<LineStation> updateLineStationAndSection(SectionAddType sectionAddType, LineStation lineStation) {
        if (sectionAddType.equals(SectionAddType.NEW_UP)) {
            lineStations.updateFirstLineStation(lineStation);
            lineStation.update(lineStation.getPreStation(), null, lineStation.getDistance());
        }
        if (sectionAddType.equals(SectionAddType.NEW_BETWEEN)) {
            LineStation updateTargetLineStation = lineStations.findLineStationByPreStation(lineStation);
            updateTargetLineStation.validDistance(lineStation);
            return Optional.of(updateTargetLineStation);
        }
        return Optional.empty();
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
                Objects.equals(lineStations, line.lineStations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, lineStations);
    }
}
