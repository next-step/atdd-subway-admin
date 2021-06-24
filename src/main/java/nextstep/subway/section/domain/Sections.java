package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> values = new ArrayList<>();

    public void add(Section section) {
        this.values.add(section);
    }

    public List<Station> toStations() {
        LinkedList<Section> storage = new LinkedList<>();
        Section section = values.get(0);
        storage.add(section);
        addAllUpSections(storage, section);
        addAllDownSections(storage, section);
        return storage.stream()
                .map(Section::toStation)
                .flatMap(Collection::stream)
                .distinct()
                .collect(toList());
    }

    private void addAllUpSections(LinkedList<Section> storage, Section section) {
        storage.addFirst(section);
        Optional<Section> foundSection = findSectionByUpStation(section.getUpStation());
        if(foundSection.isPresent()) {
            addAllUpSections(storage, foundSection.get());
        }
    }

    private void addAllDownSections(LinkedList<Section> storage, Section section) {
        Optional<Section> foundSection = findSectionByDownStation(section.getDownStation());
        if(foundSection.isPresent()) {
            storage.addLast(foundSection.get());
            addAllDownSections(storage, foundSection.get());
        }
    }

    private Optional<Section> findSectionByUpStation(Station upStation) {
        return values.stream()
                .filter(section -> section.getDownStation().getId() == upStation.getId())
                .findFirst();
    }

    private Optional<Section> findSectionByDownStation(Station downStation) {
        return values.stream()
                .filter(section -> section.getUpStation().getId() == downStation.getId())
                .findFirst();
    }
}
