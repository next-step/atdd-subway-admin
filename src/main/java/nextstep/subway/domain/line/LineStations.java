package nextstep.subway.domain.line;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class LineStations {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private List<LineStation> lineStations = new ArrayList<>();

    public void addLineStation(LineStation lineStation) {
        this.lineStations.add(lineStation);
    }

    public List<LineStation> getValues() {
        return this.lineStations;
    }
}
