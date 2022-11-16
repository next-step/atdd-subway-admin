package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class LineStations {

    @OneToMany(mappedBy = "line")
    private List<LineStation> lineStations = new ArrayList<>();

    public void addAll(List<LineStation> lineStations) {
        this.lineStations.addAll(lineStations);
    }

    public Optional<LineStation> findSameUpStation(Station upStation) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.isEqualsUpStation(upStation))
                .findFirst();
    }

    public Optional<LineStation> findSameDownStation(Station downStation) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.isEqualsDownStation(downStation))
                .findFirst();
    }
}
