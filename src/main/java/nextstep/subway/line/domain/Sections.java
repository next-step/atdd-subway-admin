package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections(Section section) {
        sections.add(section);
    }

    protected Sections() {
    }

    public List<Station> getStations() {
        Station station = findFirstUpStation();
        List<Station> result = new ArrayList<>(Collections.singletonList(station));
        Optional<Section> nextSection = findNextSection(station);
        while (nextSection.isPresent()) {
            Station nextStation = nextSection.get().getDownStation();
            result.add(nextStation);
            nextSection = findNextSection(nextStation);
        }
        return result;
    }

    private Station findFirstUpStation() {
        return sections.stream()
                .map(Section::getUpStation)
                .filter(this::matchUpStation)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("상행역이 존재하지 않습니다."));
    }

    private boolean matchUpStation(Station upStation) {
        return sections.stream()
                .noneMatch(section -> section.getDownStation() == upStation);
    }

    private Optional<Section> findNextSection(Station station) {
        return sections.stream()
                .filter(s -> s.getUpStation() == station)
                .findAny();
    }
}
