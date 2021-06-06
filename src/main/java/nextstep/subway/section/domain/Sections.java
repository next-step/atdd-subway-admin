package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    public Sections() {
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(section -> section.getStation().stream())
                .distinct()
                .collect(toList());
    }
}
