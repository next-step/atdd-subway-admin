package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        sections.stream()
                .filter(existSection -> existSection.isSameDownStation(section))
                .findFirst()
                .ifPresent(existSection -> existSection.replaceDownStation(section));

        sections.stream()
                .filter(existSection -> existSection.isSameUpStation(section))
                .findFirst()
                .ifPresent(existSection -> existSection.replaceUpStation(section));

        sections.add(section);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List<Station> getStations() {
        return sections.stream()
                .map(section -> section.getStations())
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }
    /*
    public List<Station> getStationsOrder() {
        Optional<Section> section = sections.stream()
                .findFirst();
        
        findUpToDownStation(section);
        
    }

    private List<Station> findUpToDownStation(Optional<Section> section) {
    }*/
}
