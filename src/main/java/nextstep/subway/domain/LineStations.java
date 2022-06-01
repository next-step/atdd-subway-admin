package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class LineStations {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineStation> lineStations = new ArrayList<>();

    public LineStations() {
    }

    public LineStations(final List<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public void add(final LineStation lineStation) {
        lineStations.add(lineStation);
    }

    public List<Station> getStations() {
        return lineStations
                .stream()
                .map(LineStation::getStation)
                .collect(Collectors.toList());
    }
}
