package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        sections.add(section);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public List<Station> findStationInSections() {
        List<Station> stations = new ArrayList<>();
        Section firstSection = findFirstSection();
        stations.add(firstSection.getUpStation());
        stations.addAll(findOthersStations(firstSection.getDownStation()));
        return stations;
    }

    public List<Station> findOthersStations(Station downStation) {
        List<Station> stations = new ArrayList<>();
        stations.add(downStation);
        Section nextSection = findSectionInUpStation(downStation);
        while (!Objects.isNull(nextSection)) {
            stations.add(nextSection.getDownStation());
            nextSection = findSectionInUpStation(nextSection.getDownStation());
        }
        return new ArrayList<>(stations);
    }

    public Section findFirstSection() {
        return sections.stream()
                .filter(section -> Objects.isNull(findSectionInDownStation(section.getUpStation())))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public Section findSectionInDownStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.getDownStation() == upStation)
                .findFirst()
                .orElse(null);
    }

    public Section findSectionInUpStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.getUpStation() == downStation)
                .findFirst()
                .orElse(null);
    }
}
