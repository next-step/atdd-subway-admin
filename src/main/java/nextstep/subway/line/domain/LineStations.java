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
import static nextstep.subway.section.domain.Distance.*;

public class LineStations {
    private final Map<Station, LineStation> lineStations;

    public LineStations(List<LineStation> lineStations) {
        this.lineStations = unmodifiableMap(lineStations.stream()
                .collect(toMap(LineStation::getStation, Function.identity())));
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
                .filter(ls -> ls.getPreviousStation() == null)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("기점 지하철역이 존재하지 않습니다"));
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

        if (!isValidTarget(upLineStation, downLineStation)) {
            throw new IllegalArgumentException("상행 역과 하행 역 둘 중 하나라도 포함되어 있지 않으면 추가할 수 없습니다");
        }

        if (upLineStation.isPersistence() && downLineStation.isNew()) {
            addDownLineStation(upLineStation, downLineStation, distance); // 기점 -> new, 종점 -> new 경우
        } else if (upLineStation.isNew() && downLineStation.isPersistence()) {
            addUpLineStation(upLineStation, downLineStation, distance); // new -> 기점, new -> 종점 경우
        }

        return getNewLineStationForSave(upLineStation, downLineStation);
    }

    private boolean isValidTarget(LineStation upLineStation, LineStation downLineStation) {
        return Stream.of(upLineStation, downLineStation)
                .anyMatch(ls -> ls.isPersistence() && ls.isFirst() || ls.isPersistence() && ls.isLast());
    }

    private void addDownLineStation(LineStation persistenceLineStation, LineStation newLineStation, Distance distance) {
        if (persistenceLineStation.isFirst()) {
            LineStation oldNext = getLineStationByStation(persistenceLineStation.getNextStation());

            newLineStation.applyPreviousStationAndNextStation(persistenceLineStation.getStation(), oldNext.getStation());
            oldNext.applyPreviousStation(newLineStation.getStation());
            applyDistance(persistenceLineStation, newLineStation, distance);
        } else if (persistenceLineStation.isLast()) {
            newLineStation.applyPreviousStation(persistenceLineStation.getStation());
            persistenceLineStation.applyDistanceForNextStation(distance);
            newLineStation.applyDistanceForNextStation(new Distance(MIN_DISTANCE));
        }
        persistenceLineStation.applyNextStation(newLineStation.getStation());
    }

    private void addUpLineStation(LineStation newLineStation, LineStation persistenceLineStation, Distance distance) {
        if (persistenceLineStation.isFirst()) {
            newLineStation.applyNextStation(persistenceLineStation.getStation());
            newLineStation.applyDistanceForNextStation(distance);
        } else if (persistenceLineStation.isLast()) {
            LineStation oldPrevious = getLineStationByStation(persistenceLineStation.getPreviousStation());

            newLineStation.applyPreviousStationAndNextStation(oldPrevious.getStation(), persistenceLineStation.getStation());
            oldPrevious.applyNextStation(newLineStation.getStation());
            applyDistance(oldPrevious, newLineStation, distance);
        }
        persistenceLineStation.applyPreviousStation(newLineStation.getStation());
    }

    private void applyDistance(LineStation lineStation, LineStation newLineStation, Distance newDistance) {
        if (newDistance.isDistanceGreaterThanEqual(lineStation.getDistanceForNextStation())) {
            throw new IllegalArgumentException("기존 구간의 길이보다 작아야 합니다");
        }
        lineStation.changeDistanceForNextStation(newDistance);
        newLineStation.applyDistanceForNextStation(newDistance);
    }

    private LineStation getNewLineStationForSave(LineStation upLineStation, LineStation downLineStation) {
        return Stream.of(upLineStation, downLineStation)
                .filter(LineStation::isNew)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("상행 역과 하행 역이 이미 노선에 모두 등록되어 있는 상황입니다"));
    }
}
