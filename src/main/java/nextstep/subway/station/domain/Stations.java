package nextstep.subway.station.domain;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Stations {
    @OneToMany
    @JoinColumn
    private List<Station> stations = new ArrayList<>();

    public List<Station> getStations() {
        return stations;
    }
}
