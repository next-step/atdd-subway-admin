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

    public List<Station> getStationsInOrder() {
        Optional<Section> section = sections.stream()
                .findFirst();
        
        List<Station> result = findDownToUpStation(section);
        result.addAll(findUpToDownStation(section));
        return result;
    }

    private List<Station> findUpToDownStation(Optional<Section> section) {
        List<Station> result = new ArrayList<>();
        while (section.isPresent()) {
            Station downStation = section.get().getDownStation();
            result.add(downStation);
            section = sections.stream()
                    .filter(existSection -> existSection.getUpStation() == downStation)
                    .findFirst();
        }

        return result;
    }

    private List<Station> findDownToUpStation(Optional<Section> section) {
        List<Station> result = new ArrayList<>();
        while (section.isPresent()) {
            Station upStation = section.get().getUpStation();
            result.add(upStation);
            section = sections.stream()
                    .filter(existSection -> existSection.getDownStation() == upStation)
                    .findFirst();
        }
        Collections.reverse(result);
        return result;
    }
}
