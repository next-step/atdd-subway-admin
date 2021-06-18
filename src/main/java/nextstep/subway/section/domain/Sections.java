package nextstep.subway.section.domain;

import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Station> getStations() {
        return sections.stream().map(Section::getUpDownStation)
                .reduce((currentUpDownStation, nextUpDownStation) -> {
                    Set<Station> set = new HashSet<>();
                    set.addAll(currentUpDownStation);
                    set.addAll(nextUpDownStation);
                    return new ArrayList<>(set);
                }).orElseThrow(NotFoundStationException::new);
    }
}
