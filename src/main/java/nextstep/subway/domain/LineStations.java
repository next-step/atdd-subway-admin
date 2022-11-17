package nextstep.subway.domain;

import nextstep.subway.exception.EntityNotFoundException;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class LineStations {

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private List<LineStation> lineStations = new ArrayList<>();

    public void add(LineStation lineStation) {
        this.lineStations.add(lineStation);
    }

    public void addSection(LineStation newLineStation) {
        LineStation station = getLineStationByStandardStation(newLineStation);
        station.resetPreStation(newLineStation.getStation());
        add(newLineStation);
    }

    private LineStation getLineStationByStandardStation(LineStation newLineStation) {
        if (isStandardDownStation(newLineStation)) {
            return lineStations.stream()
                    .filter(lineStation -> lineStation.getStation().equals(newLineStation.getStation()))
                    .findFirst().get();
        }
        return lineStations.stream()
                .filter(lineStation -> lineStation.getStation().equals(newLineStation.getPreStation()))
                .findFirst().get();
    }

    private boolean isStandardDownStation(LineStation newLineStation) {
        return lineStations.stream()
                .anyMatch(lineStation -> lineStation.getStation().getId().equals(newLineStation.getStation().getId()));
    }

    public List<LineStation> getList() {
        return lineStations;
    }

    public LineStation findUpLineStation() {
        return lineStations.stream()
                .filter(lineStation -> lineStation.getPreStation() == null)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("LineStation", null));
    }

}
