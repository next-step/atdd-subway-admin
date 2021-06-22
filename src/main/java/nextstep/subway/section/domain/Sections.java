package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Station> toStations() {
        return sections.stream()
                .map(Section::toStation)
                .flatMap(Collection::stream)
                .distinct()
                .collect(toList());
    }
}
