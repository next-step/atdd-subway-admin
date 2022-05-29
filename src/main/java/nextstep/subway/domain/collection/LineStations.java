package nextstep.subway.domain.collection;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.Station;

@Embeddable
public class LineStations {

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "line_id",foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private List<LineStation> lineStations = new ArrayList<>();

    public Set<Station> includeStations() {
        Set<Station> stations = new LinkedHashSet<>();
        for(LineStation lineStation : lineStations){
            stations.add(lineStation.getUpStation());
            stations.add(lineStation.getDownStation());
        }
        return stations;
    }

    public void add(LineStation lineStation) {
        lineStations.add(lineStation);
    }
}
