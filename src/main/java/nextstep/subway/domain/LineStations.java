package nextstep.subway.domain;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class LineStations {
    private static final int ONE = 1;

    @OneToMany(mappedBy = "line", orphanRemoval = true)
    private final List<LineStation> lineStations = new ArrayList<>();

    protected LineStations() {

    }

    public LineStations(List<LineStation> lineStations) {
        validation(lineStations);
        this.lineStations.addAll(lineStations);
    }

    public void addLineStation(final LineStation lineStation) {
        Optional<LineStation> isLineStation = validation(lineStation);
        isLineStation.ifPresent(station -> station.updateLineStation(lineStation));
        this.lineStations.add(lineStation);
    }

    public boolean isContains(final LineStation lineStation) {
        return this.lineStations.contains(lineStation);
    }

    public LineStations getLineStationBySorted() {
        List<LineStation> result = new ArrayList<>();
        findStartStation().ifPresent(startStation -> {
            result.add(startStation);
            insertLineStationBySorted(result, startStation);
        });
        return new LineStations(result);
    }

    public List<Station> getStation() {
        List<LineStation> lineStationList = getLineStationBySorted().getLineStations();
        return lineStationList.stream().map(LineStation::getCurrentStation).collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return getLineStations().stream().map(LineStation::getSection).collect(Collectors.toList());
    }

    public List<LineStation> getLineStations() {
        return lineStations;
    }

    public List<Station> getSortedStationsByStationId() {
        List<Station> stations = this.lineStations.stream().map(lineStation -> Arrays.asList(lineStation.getPreStation(), lineStation.getCurrentStation()))
                .collect(ArrayList::new, List::addAll, List::addAll);
        return stations.stream().distinct().sorted().collect(Collectors.toList());
    }

    private void insertLineStationBySorted(List<LineStation> result, LineStation startStation) {
        Optional<LineStation> preStation = comparePreStation(startStation.getCurrentStation());
        while (preStation.isPresent()) {
            LineStation station = preStation.get();
            result.add(station);
            preStation = findPreStation(station.getCurrentStation());
        }
    }

    private Optional<LineStation> validation(final LineStation lineStation) {
        if (this.lineStations.isEmpty()) {
            return Optional.empty();
        }
        Optional<LineStation> isPreStation = comparePreStation(lineStation.getPreStation());
        Optional<LineStation> isCurrentStation = compareCurrentStation(lineStation.getCurrentStation());
        if (Objects.equals(isPreStation.isPresent(), isCurrentStation.isPresent())) {
            throw new IllegalArgumentException("invalid Argument");
        }
        return isPreStation.isPresent() ? isPreStation : isCurrentStation;
    }

    private Optional<LineStation> findStartStation() {
        return this.lineStations.stream().filter(LineStation::isStartStation).findFirst();
    }

    private Optional<LineStation> findPreStation(final Station station) {
        return this.lineStations.stream()
                .filter(savedLineStation -> savedLineStation.isPreStation(station))
                .findFirst();
    }

    private Optional<LineStation> comparePreStation(final Station station) {
        final List<LineStation> searchPreStation = this.lineStations.stream()
                .filter(savedLineStation -> savedLineStation.isPreStation(station) || savedLineStation.isCurrentStation(station))
                .collect(Collectors.toList());
        if (searchPreStation.size() > ONE) {
            findStartStation().ifPresent(searchPreStation::remove);
        }
        return searchPreStation.isEmpty() ? Optional.empty() : searchPreStation.stream().findFirst();
    }

    private Optional<LineStation> compareCurrentStation(final Station station) {
        return this.lineStations.stream()
                .filter(savedLineStation -> savedLineStation.isCurrentStation(station))
                .findFirst();
    }

    private void validation(List<LineStation> lineStations) {
        if (Objects.isNull(lineStations)) {
            throw new IllegalArgumentException("invalid argument");
        }
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
