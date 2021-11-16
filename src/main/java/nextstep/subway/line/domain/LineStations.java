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
    private final List<LineStation> stations = new ArrayList<>();

    public LineStations() {
    }

    public void addLineStation(LineStation lineStation) {
        validation(lineStation);
        stations.add(lineStation);
    }

    private void validation(LineStation lineStation) {
        validateDuplicateStations(lineStation);

    }

    private void validateDuplicateStations(LineStation newLineStation) {
        stations.forEach(station -> {
            if (station.isSame(newLineStation)) {
                throw new DuplicateLineStationException(ErrorCode.ALREADY_EXIST_ENTITY,"상행과 하행이 모두 이미 존재합니다.");
            }
        });
    }

    public List<Station> getStations() {
        Set<Station> result = new LinkedHashSet<>();
        stations.forEach(station -> {
            result.add(station.getPreStation());
            result.add(station.getNextStation());
        });
        return new ArrayList<>(result);
    }

}
