package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class LineStations {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineStation> lineStations;

    protected LineStations() {
        this.lineStations = new ArrayList<>();
    }

    public LineStations(List<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public void add(LineStation newLineStation) {
        LineStation upperLineStation = lineStations.stream()
                .filter(s -> s.canAddInterLineStation(newLineStation))
                .findFirst()
                .get();
        lineStations.add(newLineStation);
        upperLineStation.arrangeInterLineStation(newLineStation);
    }

    public List<Station> getStationsInOrder() {
        return getLineStationsInOrder().stream()
                .map(LineStation::getDownStation)
                .filter(downStation -> downStation != null)
                .collect(Collectors.toList());
    }

    private List<LineStation> getLineStationsInOrder() {
        Optional<LineStation> preStation = lineStations.stream()
                .filter(lineStation -> lineStation.getUpStation() == null)
                .findFirst();

        List<LineStation> result = new ArrayList<>();
        while (preStation.isPresent()) {
            LineStation lineStation = preStation.get();
            result.add(lineStation);
            preStation = lineStations.stream()
                    .filter(candidate -> isNextLineStation(lineStation, candidate))
                    .findFirst();
        }
        return result;
    }

    private boolean isNextLineStation(LineStation lineStation, LineStation candidate) {
        if (lineStation.getDownStation() == null) {
            return false;
        }

        if (candidate.getUpStation() == null) {
            return false;
        }

        return lineStation.getDownStation().getId().equals(candidate.getUpStation().getId());
    }
}
