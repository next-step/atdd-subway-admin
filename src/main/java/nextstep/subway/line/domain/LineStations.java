package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class LineStations {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<LineStation> lineStations;

    public LineStations() {
        this.lineStations = new ArrayList<>();
    }

    void add(LineStation lineStation) {
        if (contains(lineStation)) {
            return;
        }
        this.lineStations.add(lineStation);
    }

    public List<LineStation> getSortedList() {
        return Collections.unmodifiableList(
            this.lineStations.stream()
                .sorted()
                .collect(Collectors.toList()));
    }

    boolean contains(LineStation lineStation) {
        return this.lineStations.contains(lineStation);
    }

    void remove(LineStation lineStation) {
        this.lineStations.remove(lineStation);
    }

    LineStation findByStation(Station station) {
        return this.lineStations.stream()
            .filter(section -> section.hasStation(station))
            .findFirst().orElse(null);
    }

    LineStation findByNextStation(Station linkStation) {
        return this.lineStations.stream()
            .filter(section -> section.hasNextStation(linkStation))
            .findFirst().orElse(null);
    }

}
