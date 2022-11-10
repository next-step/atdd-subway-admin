package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class LineStations {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private List<Station> lineStations = new ArrayList<>();

    public void add(Station station) {
        lineStations.add(station);
    }

    public List<Station> getLineStations() {
        return lineStations;
    }
}
