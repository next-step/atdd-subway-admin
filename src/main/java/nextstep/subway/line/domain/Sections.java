package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> sections;

    public Sections() {
        this.sections = new ArrayList<>();
    }

    public void add(Section section) {
        sections.add(section);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List<Station> getStations() {
        return sections.stream()
            .map(Section::asStations)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }


}
