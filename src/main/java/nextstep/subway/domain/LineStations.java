package nextstep.subway.domain;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class LineStations {
    @OneToMany(mappedBy = "line", orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<LineStation> lineStations = new ArrayList<>();

    protected LineStations() {

    }

    public LineStations(List<LineStation> lineStations) {
        if (Objects.isNull(lineStations)) {
            throw new IllegalArgumentException("invalid argument");
        }
        this.lineStations.addAll(lineStations);
    }

    public void addLineStation(final LineStation lineStation) {
        if (!this.lineStations.isEmpty()) {
            LineStation findLineStation = findInsertPosition(lineStation).orElseThrow(IllegalArgumentException::new);
            findLineStation.updateLineStation(lineStation);
        }
        this.lineStations.add(lineStation);
    }

    public boolean isContains(final LineStation lineStation) {
        return this.lineStations.contains(lineStation);
    }

    public LineStations getSortedLineStations() {
        List<LineStation> result = new ArrayList<>();
        findStartStation().ifPresent(startStation -> {
            result.add(startStation);
            insertLineStationBySorted(result, startStation);
        });
        return new LineStations(result);
    }

    public List<Station> getStations() {
        List<LineStation> lineStationList = getSortedLineStations().getLineStations();
        return lineStationList.stream().map(LineStation::getCurrentStation).collect(Collectors.toList());
    }

    public List<Station> getSortedStationsByStationId() {
        List<Station> stations = this.lineStations.stream()
                .map(lineStation -> Arrays.asList(lineStation.getPreStation(), lineStation.getCurrentStation()))
                .collect(ArrayList::new, List::addAll, List::addAll);
        return stations.stream().distinct().sorted().collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return getLineStations().stream().map(LineStation::getSection).collect(Collectors.toList());
    }

    public List<LineStation> getLineStations() {
        return lineStations;
    }


    private void insertLineStationBySorted(List<LineStation> result, LineStation startStation) {
        Optional<LineStation> preStation = findByComparePreStation(startStation.getCurrentStation());
        while (preStation.isPresent()) {
            LineStation station = preStation.get();
            result.add(station);
            preStation = findPreStation(station.getCurrentStation());
        }
    }

    private Optional<LineStation> findInsertPosition(LineStation lineStation) {
        Optional<LineStation> isMiddle = isInsertPositionMiddle(lineStation);
        if (isMiddle.isPresent()) return isMiddle;
        Optional<LineStation> isStart = isInsertPositionStart(lineStation);
        if (isStart.isPresent()) return isStart;
        return isInsertEnd(lineStation);
    }

    private Optional<LineStation> isInsertEnd(LineStation lineStation) {
        Optional<LineStation> isMatchCurrentAndPreStation = findByCompareCurrentStation(lineStation.getPreStation());
        Optional<LineStation> isMatchCurrentAndCurrentStation = findByCompareCurrentStation(lineStation.getCurrentStation());
        if (isMatchCurrentAndPreStation.isPresent() && !isMatchCurrentAndCurrentStation.isPresent()) {
            return isMatchCurrentAndPreStation;
        }
        return Optional.empty();
    }

    private Optional<LineStation> isInsertPositionStart(LineStation lineStation) {
        Optional<LineStation> isMatchPreAndPreStation = findByComparePreStation(lineStation.getPreStation());
        Optional<LineStation> isMatchPreAndCurrentStation = findByComparePreStation(lineStation.getCurrentStation());
        if (!isMatchPreAndPreStation.isPresent() && isMatchPreAndCurrentStation.isPresent()) {
            return isMatchPreAndCurrentStation;
        }
        return Optional.empty();
    }

    private Optional<LineStation> isInsertPositionMiddle(final LineStation lineStation) {
        Optional<LineStation> isMatchPreAndPreStation = findByComparePreStation(lineStation.getPreStation());
        Optional<LineStation> isMatchCurrentAndCurrentStation = findByCompareCurrentStation(lineStation.getCurrentStation());
        if (isMatchPreAndPreStation.isPresent() && !isMatchCurrentAndCurrentStation.isPresent()) {
            return isMatchPreAndPreStation;
        }
        return Optional.empty();
    }

    private Optional<LineStation> findStartStation() {
        return this.lineStations.stream().filter(LineStation::isStartStation).findFirst();
    }

    private Optional<LineStation> findPreStation(final Station station) {
        return this.lineStations.stream()
                .filter(savedLineStation -> savedLineStation.isPreStation(station))
                .findFirst();
    }

    private Optional<LineStation> findByComparePreStation(final Station station) {
        return this.lineStations.stream()
                .filter(savedLineStation -> savedLineStation.isPreStation(station)).findAny();
    }

    private Optional<LineStation> findByCompareCurrentStation(final Station station) {
        return this.lineStations.stream()
                .filter(savedLineStation -> savedLineStation.isCurrentStation(station))
                .findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineStations that = (LineStations) o;
        return Objects.equals(lineStations, that.lineStations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineStations);
    }
}
