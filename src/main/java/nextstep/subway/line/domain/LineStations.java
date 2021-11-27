package nextstep.subway.line.domain;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class LineStations {

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id", nullable = false)
    private List<LineStation> lineStations = new ArrayList<>();

    protected LineStations() {
    }

    public void add(LineStation addLineStation) {
        validate(addLineStation);

        nextLineStationUpdate(addLineStation);

        lastLineStationUpdate(addLineStation);

        lineStations.add(addLineStation);
        addLastLineStation(addLineStation);
    }

    public void addLastLineStation(LineStation addLineStation) {
        Optional<LineStation> optionalLastStation = getLastLineStation();

        if (!optionalLastStation.isPresent()) {
            lineStations.add(LineStation.lastOf(addLineStation));
        }
    }

    public List<LineStation> getStations() {
        List<LineStation> result = new ArrayList<>();
        Optional<LineStation> nextLineStation = getLastLineStation();

        while (nextLineStation.isPresent()) {
            LineStation lineStation = nextLineStation.get();
            result.add(lineStation);

            nextLineStation = lineStations.stream()
                .filter(it -> lineStation.getStationId().equals(it.getNextStationId()))
                .findFirst();
        }

        Collections.reverse(result);
        return result;
    }

    public void delete() {
        for (LineStation lineStation : lineStations) {
            lineStation.delete();
        }
    }

    private void nextLineStationUpdate(LineStation addLineStation) {
        lineStations.stream()
            .filter(addLineStation::isNext)
            .findFirst()
            .ifPresent(next -> next.nextStationIdUpdate(addLineStation));
    }

    private void lastLineStationUpdate(LineStation addLineStation) {
        lineStations.stream()
            .filter(LineStation::isLast)
            .findFirst()
            .ifPresent(it -> it.stationIdUpdate(addLineStation));
    }

    private void validate(LineStation addLineStation) {
        if (!lineStations.isEmpty()) {
            validateDuplicate(addLineStation);
            validateExist(addLineStation);
        }
    }

    private void validateExist(LineStation addLineStation) {
        if (isAddableMatch(addLineStation)) {
            throw new InvalidParameterException("추가 할 수 있는 상행,하행 구간이 없습니다.");
        }
    }

    private void validateDuplicate(LineStation addLineStation) {
        if (isDuplicate(addLineStation)) {
            throw new InvalidParameterException("이미 등록된 구간이 있습니다.");
        }
    }

    private boolean isDuplicate(LineStation addLineStation) {
        return lineStations.stream()
            .anyMatch(addLineStation::isDuplicate);
    }

    private boolean isAddableMatch(LineStation addLineStation) {
        return lineStations.stream()
            .noneMatch(addLineStation::isAddableMatch);
    }

    private Optional<LineStation> getLastLineStation() {
        return lineStations.stream()
            .filter(LineStation::isLast)
            .findFirst();
    }
}
