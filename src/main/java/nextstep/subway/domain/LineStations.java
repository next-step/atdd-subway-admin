package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class LineStations {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    public List<LineStation> lineStations = new ArrayList<>();

    protected LineStations() {
    }


    public List<Station> getStations() {
        return lineStations.stream()
                .map(LineStation::getRelationStation)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }


    public void addLineStation(Station upStation, Station downStation, int distance) {
        LineStation newStation = LineStation.of(upStation, downStation, distance);
        if (lineStations.isEmpty()) {
            lineStations.add(newStation);
            return;
        }

        checkAlreadyExistStation(upStation, downStation);
        checkExistBothStation(newStation);


        lineStations.forEach(lineStation -> lineStation.update(newStation));
        lineStations.add(newStation);
    }

    private void checkAlreadyExistStation(Station upStation, Station downStation) {
        if (isSameUpStation(upStation) && isSameDownStation(downStation)) {
            throw new IllegalArgumentException("이미 존재해요");
        }
    }

    private boolean isSameUpStation(Station upStation) {
        return getStations().contains(upStation);
    }

    private boolean isSameDownStation(Station downStation) {
        return this.getStations().contains(downStation);
    }


    private void checkExistBothStation(LineStation station) {
        if (isExistStations(station)) {
            throw new IllegalArgumentException("상행성 하행선 모두 존재하지 않습니다.");
        }
    }

    private boolean isExistStations(LineStation station) {
        List<Station> stations = getStations();
        return station.getRelationStation()
                .stream()
                .noneMatch(stations::contains);
    }
}
