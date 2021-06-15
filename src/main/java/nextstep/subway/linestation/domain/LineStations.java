package nextstep.subway.linestation.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import nextstep.subway.NotFoundException;
import nextstep.subway.linestation.exception.DuplicateLineStationException;
import nextstep.subway.linestation.exception.MethodNotAllowedException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class LineStations {

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final List<LineStation> lineStations = new LinkedList<>();

    public void addLineStation(final LineStation lineStation) {
        if (lineStations.contains(lineStation)) {
            throw new DuplicateLineStationException();
        }

        lineStations.add(lineStation);
    }

    public void addLineStation(final LineStation upStation, final LineStation downStation, final int distance) {
        final List<LineStation> stations = Arrays.asList(upStation, downStation);
        final LineStation lineStation = findLineStation(validLineStation(stations));
        lineStation.update(upStation, downStation, distance);
        addLineStation(upStation, downStation, lineStation);
    }

    private void addLineStation(final LineStation upStation, final LineStation downStation, final LineStation lineStation) {
        int index = lineStations.indexOf(lineStation);
        if (!lineStations.contains(upStation)) {
            lineStations.add(index, upStation);
        }

        if (!lineStations.contains(downStation)) {
            lineStations.add(++index, downStation);
        }
    }

    private LineStation validLineStation(final List<LineStation> stations) {
        final List<LineStation> lineStations = findStations(stations);

        if (lineStations.size() != 1) {
            throw new IllegalArgumentException();
        }

        return lineStations.get(0);
    }

    public List<LineStation> findStations(final List<LineStation> stations) {
        return stations.stream()
            .filter(this::containsStation)
            .collect(Collectors.toList());
    }

    public boolean containsStation(final LineStation lineStation) {
        return lineStations.stream()
            .anyMatch(ls -> ls.equals(lineStation));
    }

    private LineStation findLineStation(final LineStation lineStation) {
        return lineStations.stream()
            .filter(ls -> ls.equals(lineStation))
            .findFirst()
            .orElseThrow(NotFoundException::new);
    }

    public void removeStation(final LineStation lineStation) {
        validateLineStationsSize();
        final LineStation currentStation = findLineStation(lineStation);
        currentStation.mergePrevAndNext();
        lineStations.remove(currentStation);
    }

    private void validateLineStationsSize() {
        if (lineStations.size() == 2) {
            throw new MethodNotAllowedException();
        }
    }

    public List<Station> orderedStations() {
        Optional<LineStation> optLIneStation = lineStations.stream()
            .filter(lineStation -> !lineStation.getPreviousStation().isPresent())
            .findFirst();

        final List<Station> stations = new ArrayList<>();
        while (optLIneStation.isPresent()) {
            final LineStation lineStation = optLIneStation.get();
            final Station station = lineStation.getStation();
            stations.add(station);

            optLIneStation = lineStation.getNextStation();
        }

        return stations;
    }
}
