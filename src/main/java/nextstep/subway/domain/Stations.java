package nextstep.subway.domain;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Stations {
    @OneToMany
    private List<Station> values = new ArrayList<>();

    public void addStations(Station upStation, Station downStation) {
        this.values.add(upStation);
        this.values.add(downStation);
    }

    public List<Station> getValues() {
        return values;
    }
}
