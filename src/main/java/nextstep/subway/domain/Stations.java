package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Stations {
    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST)
    private final List<Station> stations;

    public Stations() {
        this.stations = new ArrayList<>();
    }
    public Stations(Station station) {
        this.stations = new ArrayList<>();
        this.stations.add(station);
    }

    public Stations(List<Station> stations) {
        this.stations = new ArrayList<>(stations);
    }

    public List<Station> asList() {
        return this.stations;
    }
}
