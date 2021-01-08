package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Distance;
import nextstep.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static nextstep.subway.line.domain.PositionStatus.*;

public class LineStations {
    private static final int MIN_DISTANCE = 0;
    private static final int MIN_STATION_COUNT = 2;
    private final Map<Station, LineStation> lineStations;

    public LineStations(List<LineStation> lineStations) {
        if (lineStations.isEmpty()) {
            throw new IllegalStateException("구간에 등록된 역이 존재하지 않습니다");
        }
        this.lineStations = unmodifiableMap(lineStations.stream()
                .collect(toMap(LineStation::getStation, Function.identity())));
    }

    public void linkFirstAndLastLineStations(Distance distance) {
        LineStation first = getFirstLineStation();
        LineStation last = getLastLineStation();

        first.applyNextStation(last);
        first.applyDistanceForNextStation(distance);
        last.applyDistanceForNextStation(new Distance(MIN_DISTANCE));
    }

    private void linkFirstAndNewLineStations(LineStation newLineStation, Distance distance) {
        LineStation first = getFirstLineStation();
        LineStation other = getLineStationByStation(first.getNextStation());

        first.applyNextStation(newLineStation);
        first.changeDistanceForNextStation(distance);
        newLineStation.changePositionStatus(MIDDLE);
        newLineStation.applyNextStation(other);
        newLineStation.applyDistanceForNextStation(distance);
    }

    private void linkNewAndFirstLineStations(LineStation newLineStation, Distance distance) {
        LineStation first = getFirstLineStation();

        newLineStation.changePositionStatus(FIRST);
        newLineStation.applyNextStation(first);
        newLineStation.applyDistanceForNextStation(distance);
        first.changePositionStatus(MIDDLE);
    }

    private void linkNewAndLastLineStations(LineStation newLineStation, Distance distance) {
        LineStation last = getLastLineStation();
        LineStation other = getLineStationByStation(last.getPreviousStation());

        other.applyNextStation(newLineStation);
        other.changeDistanceForNextStation(distance);
        newLineStation.changePositionStatus(MIDDLE);
        newLineStation.applyNextStation(last);
        newLineStation.applyDistanceForNextStation(distance);
    }

    private void linkLastAndNewLineStations(LineStation newLineStation, Distance distance) {
        LineStation last = getLastLineStation();

        last.changePositionStatus(MIDDLE);
        last.applyNextStation(newLineStation);
        last.applyDistanceForNextStation(distance);
        newLineStation.changePositionStatus(LAST);
    }

    public List<Station> getSortedStations() {
        List<LineStation> lineStations = new ArrayList<>();
        addLineStationToLineStations(lineStations);
        return lineStations.stream()
                .map(LineStation::getStation)
                .collect(toList());
    }

    private LineStation getLineStationByStation(Station station) {
        return lineStations.get(station);
    }

    private LineStation getFirstLineStation() {
        return lineStations.values()
                .stream()
                .filter(LineStation::isFirst)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("기점 지하철역이 존재하지 않습니다"));
    }

    private LineStation getLastLineStation() {
        return lineStations.values()
                .stream()
                .filter(LineStation::isLast)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("종점 지하철역이 존재하지 않습니다"));
    }

    private void addLineStationToLineStations(List<LineStation> lineStations) {
        LineStation firstLineStation = getFirstLineStation();
        lineStations.add(firstLineStation);
        LineStation nextLineStation = getLineStationByStation(firstLineStation.getNextStation());

        while (nextLineStation != null) {
            lineStations.add(nextLineStation);
            nextLineStation = getLineStationByStation(nextLineStation.getNextStation());
        }
    }

    public LineStation addNewLineStation(Line line, Station upStation, Station downStation, Distance distance) {
        LineStation upLineStation = Optional.ofNullable(getLineStationByStation(upStation)).orElse(new LineStation(line, upStation));
        LineStation downLineStation = Optional.ofNullable(getLineStationByStation(downStation)).orElse(new LineStation(line, downStation));

        LineStation newLineStation = getNewLineStation(upLineStation, downLineStation);

        if (upLineStation.isPersistence() && downLineStation.isNew()) {
            linkNewLineStationAsDownLineStation(distance, upLineStation, newLineStation);
            return newLineStation;
        }

        linkNewLineStationAsUpLineStation(distance, downLineStation, newLineStation);
        return newLineStation;
    }

    private void linkNewLineStationAsDownLineStation(Distance distance, LineStation upLineStation, LineStation newLineStation) {
        if (upLineStation.isFirst()) {
            linkFirstAndNewLineStations(newLineStation, distance);
            return;
        }
        linkLastAndNewLineStations(newLineStation, distance);
    }

    private void linkNewLineStationAsUpLineStation(Distance distance, LineStation downLineStation, LineStation newLineStation) {
        if (downLineStation.isLast()) {
            linkNewAndLastLineStations(newLineStation, distance);
            return;
        }
        linkNewAndFirstLineStations(newLineStation, distance);
    }

    private LineStation getNewLineStation(LineStation upLineStation, LineStation downLineStation) {
        if (Stream.of(upLineStation, downLineStation).allMatch(LineStation::isNew)) {
            throw new IllegalArgumentException("상행 역과 하행 역 전부 신규 역 입니다");
        }
        return Stream.of(upLineStation, downLineStation)
                .filter(LineStation::isNew)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상행 역과 하행 역 둘 중 하나라도 포함되어 있지 않으면 추가할 수 없습니다"));
    }

    public LineStation removeSection(Station station) {
        checkSectionForRemoving();
        LineStation removedLineStation = getLineStationByStation(station);

        if (removedLineStation.isFirst()) {
            return changeLineStationStatus(removedLineStation, removedLineStation.getNextStation(), FIRST);
        }

        if (removedLineStation.isLast()) {
            return changeLineStationStatus(removedLineStation, removedLineStation.getPreviousStation(), LAST);
        }

        LineStation previousLineStation = getLineStationByStation(removedLineStation.getPreviousStation());
        LineStation nextLineStation = getLineStationByStation(removedLineStation.getNextStation());
        previousLineStation.applyNextStation(nextLineStation);
        previousLineStation.mergeDistanceForNextStation(removedLineStation.getDistanceForNextStation());
        return removedLineStation;
    }

    private LineStation changeLineStationStatus(LineStation removedLineStation, Station station, PositionStatus status) {
        LineStation lineStation = getLineStationByStation(station);
        lineStation.changePositionStatus(status);
        return removedLineStation;
    }

    private void checkSectionForRemoving() {
        if (lineStations.values().stream().noneMatch(LineStation::isMiddle)) {
            throw new IllegalStateException("현재 해당 구간에 역이 " + MIN_STATION_COUNT + "개만 존재하여 제거할 수 없습니다");
        }
    }
}
