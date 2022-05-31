package nextstep.subway.domain;

import nextstep.subway.exception.CustomException;
import nextstep.subway.exception.ErrorCode;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Embeddable
public class LineStations {

    @OneToMany
    @JoinColumn(name = "line_id")
    private List<LineStation> lineStations = new ArrayList<>();

    protected LineStations() {
    }

    public void add(LineStation lineStation) {
        if (lineStations.isEmpty()) {
            lineStations.add(lineStation);
            return;
        }

        if (hasMatchedSameStation(lineStation)) {
            throw new CustomException(ErrorCode.NON_VALID_LINE_STATION);
        }

        Optional<LineStation> matchedMiddle = getMatchedMiddle(lineStation);
        if (matchedMiddle.isPresent()) {
            updateMatchedMiddle(lineStation, matchedMiddle.get());
            lineStations.add(lineStation);
            return;
        }

        Optional<LineStation> matchedStartOrEnd = getMatchedStartOrEnd(lineStation);
        if (matchedStartOrEnd.isPresent()) {
            lineStations.add(lineStation);
            return;
        }

        throw new CustomException(ErrorCode.NON_VALID_LINE_STATION);
    }

    public List<Station> getSortedStations() {
        sort();
        List<Station> stations = new ArrayList<>();

        stations.add(lineStations.get(0).getUpStation());
        for (LineStation lineStation : lineStations) {
            stations.add(lineStation.getDownStation());
        }
        return stations;
    }

    public int size() {
        return lineStations.size();
    }

    private boolean hasMatchedSameStation(LineStation lineStation) {
        return lineStations.stream()
                .anyMatch(item -> item.equalsUpStation(lineStation.getUpStation())
                        && item.equalsDownStation(lineStation.getDownStation()));
    }

    private Optional<LineStation> getMatchedStartOrEnd(LineStation lineStation) {
        return lineStations.stream()
                .filter(item -> item.equalsUpStation(lineStation.getDownStation()) ||
                        item.equalsDownStation(lineStation.getUpStation()))
                .findAny();
    }

    private void updateMatchedMiddle(LineStation lineStation, LineStation matchedStation) {
        Long distance = calculateDistance(matchedStation, lineStation);
        if (matchedStation.equalsUpStation(lineStation.getUpStation())) {
            matchedStation.patch(distance, lineStation.getDownStation(), null);
            return;
        }
        matchedStation.patch(distance, null, lineStation.getUpStation());
    }

    private Long calculateDistance(LineStation matchedStation, LineStation lineStation) {
        if (matchedStation.getDistance() <= lineStation.getDistance()) {
            throw new CustomException(ErrorCode.NON_VALID_LINE_STATION);
        }

        return matchedStation.getDistance() - lineStation.getDistance();
    }

    private Optional<LineStation> getMatchedMiddle(LineStation lineStation) {
        return lineStations.stream()
                .filter(item -> item.equalsUpStation(lineStation.getUpStation())
                        || item.equalsDownStation(lineStation.getDownStation()))
                .findAny();
    }

    private void sort() {
        List<LineStation> sorted = new ArrayList<>();
        sorted.add(getFirst());

        Map<Station, LineStation> lineStationsByUpStation = lineStations.stream()
                .collect(Collectors.toMap(LineStation::getUpStation, Function.identity()));

        while (sorted.size() < lineStations.size()) {
            LineStation last = sorted.get(sorted.size() - 1);
            LineStation lineStation = lineStationsByUpStation.get(last.getDownStation());
            sorted.add(lineStation);
        }

        lineStations = sorted;
    }

    private LineStation getFirst() {
        Set<Station> downStationSet = lineStations.stream()
                .map(LineStation::getDownStation)
                .collect(Collectors.toSet());

        return lineStations.stream()
                .filter(item -> !downStationSet.contains(item.getUpStation()))
                .findFirst()
                .orElse(lineStations.get(0));
    }
}
