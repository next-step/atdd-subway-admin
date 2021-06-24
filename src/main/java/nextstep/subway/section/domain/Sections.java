package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> values = new ArrayList<>();

    public void add(Section section) {
        this.values.add(section);
    }

    public List<Station> toStations() {
        LinkedList<Station> storage = new LinkedList<>();
        Section section = values.get(0);
        addAllUpSections(storage, section.getUpStation());
        addAllDownSections(storage, section.getDownStation());
        return storage;
    }

    private void addAllUpSections(LinkedList<Station> storage, Station upStation) {
        storage.addFirst(upStation);
        Section foundSection = findSectionByUpStation(upStation);
        if (foundSection.getUpStation() != null) {
            addAllUpSections(storage, foundSection.getUpStation());
        }
    }

    private void addAllDownSections(LinkedList<Station> storage, Station downStation) {
        storage.addLast(downStation);
        Section foundSection = findSectionByDownStation(downStation);
        if (foundSection.getDownStation() != null) {
            addAllDownSections(storage, foundSection.getDownStation());
        }
    }

    private Section findSectionByUpStation(Station upStation) {
        return values.stream()
                .filter(section -> section.getDownStation().getId().equals(upStation.getId()))
                .findFirst().orElse(new Section());
    }

    private Section findSectionByDownStation(Station downStation) {
        return values.stream()
                .filter(section -> section.getUpStation().getId().equals(downStation.getId()))
                .findFirst().orElse(new Section());
    }
}
