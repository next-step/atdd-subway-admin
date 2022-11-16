package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class LineStations {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineStation> lineStations;

    protected LineStations() {
        this.lineStations = new ArrayList<>();
    }

    public LineStations(List<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public void add(LineStation newLineStation) {
        if (addLineStationIfFirst(newLineStation)) {
           return;
        }
        if (addLineStationIfLast(newLineStation)) {
            return;
        }
        addLineStationIfInter(newLineStation);
    }

    private boolean addLineStationIfFirst(LineStation newLineStation) {
        LineStation firstLineStation = getFirstLineStation();

        if (firstLineStation.canAddFirstLineStation(newLineStation)) {
            firstLineStation.arrangeFirstLineStation(newLineStation);
            lineStations.add(newLineStation);
            return true;
        }

        return false;
    }

    private boolean addLineStationIfLast(LineStation newLineStation) {
        LineStation lastLineStation = getLastLineStation();

        if (lastLineStation.canAddLastLineStation(newLineStation)) {
            lastLineStation.arrangeLastLineStation(newLineStation);
            lineStations.add(newLineStation);
            return true;
        }

        return false;
    }

    private void addLineStationIfInter(LineStation newLineStation) {
        LineStation upperLineStation = getUpperLineStation(newLineStation);

        if (upperLineStation.isShorterThan(newLineStation)) {
            throw new IllegalArgumentException("기존 구간의 길이보다 크거나 같습니다.");
        }

        lineStations.add(newLineStation);
        upperLineStation.arrangeInterLineStation(newLineStation);
    }

    private LineStation getFirstLineStation() {
        return lineStations.stream()
                .filter(s -> s.getUpStation() == null)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("과"));
    }

    private LineStation getLastLineStation() {
        return lineStations.stream()
                .filter(s -> s.getDownStation() == null)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상행역과 하행역 중 하나는 포함되어야 합니다."));
    }

    private LineStation getUpperLineStation(LineStation newLineStation) {
        return lineStations.stream()
                .filter(s -> s.canAddInterLineStation(newLineStation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상행역과 하행역 중 하나는 포함되어야 합니다."));
    }

    public List<Station> getStationsInOrder() {
        return getLineStationsInOrder().stream()
                .map(LineStation::getDownStation)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<LineStation> getLineStationsInOrder() {
        Optional<LineStation> preStation = lineStations.stream()
                .filter(lineStation -> lineStation.getUpStation() == null)
                .findFirst();

        List<LineStation> result = new ArrayList<>();
        while (preStation.isPresent()) {
            LineStation lineStation = preStation.get();
            result.add(lineStation);
            preStation = lineStations.stream()
                    .filter(candidate -> isNextLineStation(lineStation, candidate))
                    .findFirst();
        }

        return result;
    }

    private boolean isNextLineStation(LineStation lineStation, LineStation candidate) {
        if (lineStation.getDownStation() == null) {
            return false;
        }

        if (candidate.getUpStation() == null) {
            return false;
        }

        return lineStation.getDownStation() == candidate.getUpStation();
    }

    public boolean contains(LineStation lineStation) {
        Optional<LineStation> duplicate = lineStations.stream()
                .filter(ls -> ls.isSameLineStation(lineStation))
                .findFirst();

        return duplicate.isPresent();
    }

    public boolean contains(Station station) {
        Optional<LineStation> lineStation = lineStations.stream()
                .filter(ls -> ls.getUpStation() == station || ls.getDownStation() == station)
                .findFirst();

        return lineStation.isPresent();
    }

    public void delete(Station station) {
        if (!this.contains(station)) {
            throw new IllegalArgumentException("노선에 포함되지 않은 역은 삭제할 수 없습니다.");
        }
        if (lineStations.size() == 3) {
            throw new IllegalArgumentException("단일 구간 노선의 역은 삭제할 수 없습니다.");
        }
        LineStation prevLineStation = getPrevLineStation(station);
        LineStation nextLineStation = getNextLineStation(station);
        if (prevLineStation == null) {
            Station nextStation = nextLineStation.getDownStation();
            deleteLineStationIncludeStation(station);
            LineStation newLineStation = new LineStation(null, nextStation, 0, nextLineStation.getLine());
            lineStations.add(newLineStation);
            return;
        }
        if (nextLineStation == null) {
            Station prevStation = prevLineStation.getUpStation();
            deleteLineStationIncludeStation(station);
            LineStation newLineStation = new LineStation(prevStation, null, 0, prevLineStation.getLine());
            lineStations.add(newLineStation);
            return;
        }
        deleteLineStationIncludeStation(station);
        LineStation newLineStation = new LineStation(prevLineStation.getUpStation(), nextLineStation.getDownStation(), prevLineStation.getDistance() + nextLineStation.getDistance(), prevLineStation.getLine());
        lineStations.add(newLineStation);
    }

    private LineStation getPrevLineStation(Station station) {
        return lineStations.stream()
                .filter(candidate -> candidate.getDownStation() == station)
                .findFirst()
                .orElse(null);
    }

    private LineStation getNextLineStation(Station station) {
        return lineStations.stream()
                .filter(candidate -> candidate.getUpStation() == station)
                .findFirst()
                .orElse(null);
    }

    private void deleteLineStationIncludeStation(Station station) {
        lineStations.removeAll(lineStations.stream()
                .filter(candidate -> candidate.getUpStation() == station || candidate.getDownStation() == station)
                .collect(Collectors.toList())
        );
    }
}