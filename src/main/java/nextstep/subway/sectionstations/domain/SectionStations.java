package nextstep.subway.sectionstations.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class SectionStations {
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    private final List<SectionStation> sectionStations = new ArrayList<>();

    public void add(SectionStation sectionStation) {
        sectionStations.add(sectionStation);
    }

    public List<Station> stations() {
        return sectionStations.stream()
                .map(SectionStation::getStation)
                .collect(Collectors.toList());
    }
}
