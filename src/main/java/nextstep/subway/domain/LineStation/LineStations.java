package nextstep.subway.domain.LineStation;

import nextstep.subway.domain.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class LineStations {

    @OneToMany(
            mappedBy = "line",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<LineStation> lineStations = new ArrayList<>();

    protected LineStations() {
    }

    public static LineStations create() {
        return new LineStations();
    }

    public void add(LineStation lineStation) {
        validateDuplicateStation(lineStation.getStation());

        lineStations.add(lineStation);
    }


    private void validateDuplicateStation(Station station) {
        if (isExistSameStation(station)) {
            throw new IllegalArgumentException("이미 노선에 포함된 지하철역입니다. station : " + station);
        }
    }

    private boolean isExistSameStation(Station station) {
        return lineStations.stream()
                .map(LineStation::getStation)
                .anyMatch(storedStation -> storedStation.equals(station));
    }
}
