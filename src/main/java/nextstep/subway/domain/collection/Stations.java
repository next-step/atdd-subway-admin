package nextstep.subway.domain.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import nextstep.subway.domain.Station;

@Embeddable
public class Stations {

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_to_stations"))
    private List<Station> stations = new ArrayList<>();

    public Stations() {
    }

    public Stations(List<Station> stations) {
        this.stations = stations;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void addStaion(Station station) {
        stations.add(station);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Stations stations = (Stations) o;
        return this.stations.containsAll(stations.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations);
    }
}
