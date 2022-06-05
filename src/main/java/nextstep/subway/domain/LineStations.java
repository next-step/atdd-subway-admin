package nextstep.subway.domain;

import nextstep.subway.exception.CustomException;
import nextstep.subway.exception.ErrorCode;

import javax.persistence.CascadeType;
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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private List<LineStation> lineStations = new ArrayList<>();

    protected LineStations() {
    }

    public int size() {
        return lineStations.size();
    }

    public void add(LineStation lineStation) {
        if (lineStations.isEmpty()) {
            lineStations.add(lineStation);
            return;
        }

        if (hasMatchedSameStation(lineStation)) {
            throw new CustomException(ErrorCode.NON_VALID_CREATE_LINE_STATION);
        }

        addAboutMatchStation(lineStation);
    }

    private boolean hasMatchedSameStation(LineStation lineStation) {
        return lineStations.stream()
                .anyMatch(item -> item.equalsUpStation(lineStation.getUpStation())
                        && item.equalsDownStation(lineStation.getDownStation()));
    }

    private void addAboutMatchStation(LineStation lineStation) {
        Optional<LineStation> matchedMiddle = getMatched(lineStation.getUpStation(), lineStation.getDownStation());
        if (matchedMiddle.isPresent()) {
            updateMatchedMiddle(lineStation, matchedMiddle.get());
            lineStations.add(lineStation);
            return;
        }

        Optional<LineStation> matchedStartOrEnd = getMatched(lineStation.getDownStation(), lineStation.getUpStation());
        if (matchedStartOrEnd.isPresent()) {
            lineStations.add(lineStation);
            return;
        }

        throw new CustomException(ErrorCode.NON_VALID_CREATE_LINE_STATION);
    }

    private Optional<LineStation> getMatched(Station upStation, Station downStation) {
        return lineStations.stream()
                .filter(item -> isEqualUpStationOrDownStation(item, upStation, downStation))
                .findAny();
    }

    private boolean isEqualUpStationOrDownStation(LineStation item, Station upStation, Station downStation) {
        return item.equalsUpStation(upStation)
                || item.equalsDownStation(downStation);
    }

    private void updateMatchedMiddle(LineStation lineStation, LineStation matchedStation) {
        Long distance = calculateDistance(matchedStation, lineStation);
        if (matchedStation.equalsUpStation(lineStation.getUpStation())) {
            matchedStation.updateUpStation(distance, lineStation.getDownStation());
            return;
        }
        matchedStation.updateDownStation(distance, lineStation.getUpStation());
    }

    private Long calculateDistance(LineStation matchedStation, LineStation lineStation) {
        if (matchedStation.getDistance() <= lineStation.getDistance()) {
            throw new CustomException(ErrorCode.NON_VALID_CREATE_LINE_STATION);
        }

        return matchedStation.getDistance() - lineStation.getDistance();
    }

    public void delete(Station station) {
        validateDelete(station);
        sort();

        boolean isNotYetDeleted = true;
        for (int i = 0; i < lineStations.size() && isNotYetDeleted; i++) {
            if (deleteByUpStation(station, i)) {
                isNotYetDeleted = false;
            }
        }

        if (isNotYetDeleted) {
            deleteLastLineStation();
        }
    }

    private void validateDelete(Station station) {
        if (lineStations.size() <= 1 || hasNotMatchedSameStation(station)) {
            throw new CustomException(ErrorCode.NON_VALID_DELETE_LINE_STATION);
        }
    }

    private boolean hasNotMatchedSameStation(Station station) {
        boolean hasMatchedSameStation = lineStations.stream()
                .anyMatch(item -> isEqualUpStationOrDownStation(item, station, station));
        return !hasMatchedSameStation;
    }

    private boolean deleteByUpStation(Station station, int index) {
        LineStation willDeletedLineStation = lineStations.get(index);
        if (!willDeletedLineStation.equalsUpStation(station)) {
            return false;
        }

        if (isMiddle(index)) {
            LineStation lineStation = lineStations.get(index - 1);

            long distance = lineStation.getDistance() + willDeletedLineStation.getDistance();
            lineStation.updateDownStation(distance, willDeletedLineStation.getDownStation());
        }
        lineStations.remove(index);
        return true;
    }

    private boolean isMiddle(int index) {
        return index > 0 && index < lineStations.size();
    }

    private void deleteLastLineStation() {
        lineStations.remove(lineStations.size() - 1);
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
