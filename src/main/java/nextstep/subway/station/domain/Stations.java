package nextstep.subway.station.domain;

import static java.util.stream.Collectors.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.line.domain.Line;

@Embeddable
public class Stations {
    @OneToMany(fetch = LAZY, mappedBy = "line", cascade = MERGE)
    private Set<Station> stations = new HashSet<>();

    public Stations(List<Station> findStations) {
        this.stations = new HashSet<>(findStations);
    }

    public Stations() {
    }

    public void changeLine(Line sectionIds) {
        stations.forEach(station -> station.setLine(sectionIds));
    }

    public List<Station> getStations() {
        List<Station> sortStation = stations.stream().sorted().collect(toList());
        return Collections.unmodifiableList(sortStation);
    }
}
