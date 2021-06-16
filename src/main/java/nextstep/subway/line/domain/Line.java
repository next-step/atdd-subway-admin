package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;
import nextstep.subway.line.domain.wrappers.Distance;
import nextstep.subway.line.domain.wrappers.LineStations;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
public class Line extends BaseEntity {
    private static final String NOT_FOUND_LINE_ERROR_MESSAGE = "요청한 line id 기준 노선이 존재하지않습니다.";
    private static final String DUPLICATE_LINE_STATION_ERROR_MESSAGE = "상행역 %s 하행역 %s은 이미 등록된 구간 입니다.";
    private static final String NOT_CONTAION_STATIONS_ERROR_MESSAGE = "상행역 %s, 하행역 %s을 둘중 하나라도 포함하는 구간이 존재하지않습니다.";
    private static final String NOT_DELTED_SINGLE_SECTION_ERROR_MESSAGE = "구간이 하나만 존재하는 경우 구간을 삭제할 수 없습니다.";

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

    public void updateLineStation(LineStation lineStation) {
        if (lineStations.isNewUpLineStation(lineStation)) {
            lineStations.updateFirstLineStation(lineStation);
            lineStation.update(lineStation.getPreStation(), null, lineStation.getDistance());
            return;
        }
        if (!lineStations.isNewDownLineStation(lineStation)) {
            LineStation updateTargetLineStation = lineStations.findLineStationByPreStation(lineStation);
            Distance newDistance = updateTargetLineStation.subtractionDistance(lineStation);
            updateTargetLineStation.update(updateTargetLineStation.getStation(), lineStation.getStation(), newDistance);
        }
    }

    public LineStation findLineStationByStation(Station deleteTargetStation) {
        return lineStations.findLineStationByStation(deleteTargetStation);
    }

    public void checkValidLineStation(LineStation lineStation) {
        checkValidDuplicateLineStation(lineStation);
        checkValidNotContainStations(lineStation);
    }

    public void removeSectionByStation(LineStation lineStation) {
        lineStations.delete(lineStation);
        Optional<LineStation> nextLineStation = lineStations.findNextLineStation(lineStation);
        if (nextLineStation.isPresent()) {
            LineStation updateTargetLineStation = nextLineStation.get();
            Distance newDistance = lineStation.sumDistance(lineStation);
            updateTargetLineStation.update(updateTargetLineStation.getStation(), lineStation.getPreStation(), newDistance);
            return;
        }
        if (lineStation.isFirstLineStation()) {
            lineStations.findNextLineStation(lineStation)
                    .ifPresent(ls -> ls.update(ls.getStation(), null, ls.getDistance()));
        }
    }

    public void checkValidSingleSection() {
        if (lineStations.isSingleSection()) {
            throw new IllegalArgumentException(NOT_DELTED_SINGLE_SECTION_ERROR_MESSAGE);
        }
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
}
