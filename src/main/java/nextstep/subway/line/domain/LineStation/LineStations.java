package nextstep.subway.line.domain.LineStation;

import nextstep.subway.excetpion.ErrorCode;
import nextstep.subway.line.exception.DuplicateLineStationException;
import nextstep.subway.line.exception.LineStationNotConnectException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Embeddable
public class LineStations {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<LineStation> lineStations = new ArrayList<>();

    public LineStations() {
    }

    public void addLineStation(LineStation lineStation) {
        validation(lineStation);

        lineStations.stream()
                .filter(f -> f.getPreStation() == lineStation.getPreStation())
                .findFirst()
                .ifPresent(f -> f.updateToPreStation(lineStation));

        lineStations.add(lineStation);
    }

    private void validation(LineStation lineStation) {
        if (lineStations.size() > 0) {
            validateDuplicateStation(lineStation);
            validateConnectPossible(lineStation);
        }
    }

    private void validateDuplicateStation(LineStation lineStation) {
        lineStations.forEach(station -> {
            if (station.isSame(lineStation)) {
                throw new DuplicateLineStationException(ErrorCode.ALREADY_EXIST_ENTITY, "상행과 하행이 모두 이미 존재합니다.");
            }
        });
    }

    private void validateConnectPossible(LineStation lineStation) {
        List<Station> preStations = lineStations.stream().map(LineStation::getPreStation).collect(Collectors.toList());
        List<Station> nextStations = lineStations.stream().map(LineStation::getNextStation).collect(Collectors.toList());
        if (!preStations.contains(lineStation.getNextStation()) || !nextStations.contains(lineStation.getPreStation())) {
            throw new LineStationNotConnectException(ErrorCode.BAD_ARGUMENT, "역을 연결 할 수 없습니다.");
        }
    }

    public List<Station> getLineStations() {
        Set<Station> result = new LinkedHashSet<>();
        lineStations.forEach(station -> {
            result.add(station.getPreStation());
            result.add(station.getNextStation());
        });
        return new ArrayList<>(result);
    }

}
