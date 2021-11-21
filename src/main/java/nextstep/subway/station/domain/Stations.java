package nextstep.subway.station.domain;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.line.domain.Line;

@Embeddable
public class Stations {
    @OneToMany(fetch = LAZY, mappedBy = "line", cascade = MERGE)
    private List<Station> stations = new ArrayList<>();

    public Stations(List<Station> findStations) {
        this.stations = findStations;
    }

    public Stations() {
    }

    public void changeLine(Line sectionIds) {
        stations.forEach(station -> station.setLine(sectionIds));
    }

}
