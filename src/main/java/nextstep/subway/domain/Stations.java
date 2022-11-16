package nextstep.subway.domain;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Stations {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "line")
    private List<Station> stations = new ArrayList<>();

    public List<Station> getList() {
        return stations;
    }

    public void add(Station station) {
        this.stations.add(station);
    }

}
