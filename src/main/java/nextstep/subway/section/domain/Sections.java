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
        validateSection(stations, sectionToAdd);

        ifAddBetweenStation(stations, sectionToAdd);

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

    private void validateSection(List<Station> stations, Section sectionToAdd) {
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

    private void ifAddBetweenStation(List<Station> stations, Section sectionToAdd) {
        addOriginDownStation(stations, sectionToAdd);

        addOriginUpStation(stations, sectionToAdd);
    }

    private void addOriginDownStation(List<Station> stations, Section sectionToAdd) {
        if (stations.contains(sectionToAdd.getUpStation())) {
            findSectionByUpStation(sectionToAdd.getUpStation()).ifPresent(foundSection ->
                    foundSection.update(sectionToAdd.getDownStation(), foundSection.getDownStation(), -sectionToAdd.getDistanceValue()));
        }
    }

    private void addOriginUpStation(List<Station> stations, Section sectionToAdd) {
        if (stations.contains(sectionToAdd.getDownStation())) {
            findSectionByDownStation(sectionToAdd.getDownStation()).ifPresent(foundSection ->
                    foundSection.update(foundSection.getUpStation(), sectionToAdd.getUpStation(), -sectionToAdd.getDistanceValue()));
        }
    }

    private Optional<Section> findSectionByUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst();
    }

    private Optional<Section> findSectionByDownStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findFirst();
    }
}
