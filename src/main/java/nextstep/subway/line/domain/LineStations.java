package nextstep.subway.line.domain;

import nextstep.subway.excetpion.ErrorCode;
import nextstep.subway.line.exception.DuplicateLineStationException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Embeddable
public class LineStations {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private final List<LineStation> lineStations = new ArrayList<>();

    public LineStations() {
    }

    public void addLineStation(LineStation lineStation) {
        validation(lineStation);

        lineStations.stream()
                .filter(f -> f.getPreStation() == f.getPreStation())
                .findFirst()
                .ifPresent(f -> f.changeDistance(lineStation));

        lineStations.add(lineStation);
    }

    private void validation(LineStation lineStation) {
        validateDuplicateStation(lineStation);
    }

    private void validateDuplicateStation(LineStation lineStation) {
        lineStations.forEach(station -> {
            if (station.isSame(lineStation)) {
                throw new DuplicateLineStationException(ErrorCode.ALREADY_EXIST_ENTITY, "상행과 하행이 모두 이미 존재합니다.");
            }
        });
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
