package nextstep.subway.section.domain;

import nextstep.subway.line.exception.NotValidStationException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public void add(Section sectionToAdd) {
        List<Station> stations = getStations();
        validateStation(stations, sectionToAdd);

        isBetweenStation(stations, sectionToAdd);

        sections.add(sectionToAdd);
    }

    public void removeSection(Section section) {
        sections.remove(section);
    }

    public boolean isContainsSection(Section section) {
        return sections.contains(section);
    }

    public List<Station> getStations() {
        List<Station> stations = new LinkedList<>();

        sections.stream()
                .sorted(Section::compareTo)
                .collect(Collectors.toList())
                .forEach(section -> {
                    stations.add(section.getUpStation());
                    stations.add(section.getDownStation());
                });

        return stations.stream().distinct().collect(Collectors.toList());
    }

    private void validateStation(List<Station> stations, Section sectionToAdd) {
        if (isContainsAllAddedSection(stations, sectionToAdd)) {
            throw new NotValidStationException("구간 추가할 역이 모두 노선에 포함되어 있습니다.");
        }

        if (isNotContainsAllAddedSection(stations, sectionToAdd)) {
            throw new NotValidStationException("구간 추가할 역 중 노선에 포함되는 역이 없습니다.");
        }
    }

    private boolean isContainsAllAddedSection(List<Station> stations, Section sectionToAdd) {
        return stations.contains(sectionToAdd.getUpStation()) && stations.contains(sectionToAdd.getDownStation());
    }

    private boolean isNotContainsAllAddedSection(List<Station> stations, Section sectionToAdd) {
        return !stations.isEmpty() && !stations.contains(sectionToAdd.getUpStation()) && !stations.contains(sectionToAdd.getDownStation());
    }

    private void isBetweenStation(List<Station> stations, Section addedSection) {
        if (stations.contains(addedSection.getUpStation())) {
            findSectionByUpStation(addedSection.getUpStation()).ifPresent(foundSection ->
                    foundSection.update(addedSection.getDownStation(), foundSection.getDownStation(), -addedSection.getDistanceValue()));
        }

        if (stations.contains(addedSection.getDownStation())) {
            findSectionByDownStation(addedSection.getDownStation()).ifPresent(foundSection ->
                    foundSection.update(foundSection.getUpStation(), addedSection.getUpStation(), -addedSection.getDistanceValue()));
        }
    }

    private Optional<Section> findSectionByDownStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findFirst();
    }

    private Optional<Section> findSectionByUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst();
    }
}
