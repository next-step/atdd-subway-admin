package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class LineStations {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineStation> lineStations = new ArrayList<>();

    protected LineStations() {}

    public LineStations(List<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public void addLineStation(LineStation lineStation) {
        lineStations.add(lineStation);
    }

    public Station upEndStation() {
        return lineStations.stream()
                .map(lineStation -> lineStation.station())
                .filter(station -> station.upSection() == null)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("상행종점역이 존재하지 않습니다."));
    }

    public boolean contains(Station station) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.station().equals(station))
                .findFirst()
                .isPresent();
    }
}
