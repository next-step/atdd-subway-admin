package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public void add(Section addedSection) {
        List<Station> stations = getStations();

        if (stations.contains(addedSection.getUpStation())) {
            Section foundSection = findSectionByUpstation(addedSection.getUpStation());
            foundSection.update(addedSection.getDownStation(), foundSection.getDownStation(), foundSection.getDistance() - addedSection.getDistance());
        }
        if (stations.contains(addedSection.getDownStation())) {
            Section foundSection = findSectionByDownStation(addedSection.getDownStation());
            foundSection.update(foundSection.getUpStation(), foundSection.getUpStation(), foundSection.getDistance() - addedSection.getDistance());
        }

        sections.add(addedSection);
    }

    public void removeSection(Section section) {
        sections.remove(section);
    }

    public boolean isContainsSection(Section section) {
        return sections.contains(section);
    }

    private List<Station> getStations() {
        List<Station> stations = new LinkedList<>();
        sections.forEach(section -> {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        });
        return stations.stream().distinct().collect(Collectors.toList());
    }

    private Section findSectionByDownStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findFirst()
                .get();
    }

    private Section findSectionByUpstation(Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst()
                .get();
    }
}
