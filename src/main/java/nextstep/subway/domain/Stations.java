package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Stations {
    @OneToMany
    private List<Station> stations = new ArrayList<>();

    public void addStations(Station upStation, Station downStation) {
        this.stations.add(upStation);
        this.stations.add(downStation);
    }

    public List<Station> getStations() {
        return stations;
    }
}
