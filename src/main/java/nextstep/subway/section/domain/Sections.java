package nextstep.subway.section.domain;

import nextstep.subway.line.exception.NotValidStationException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int FIRST_INDEX = 0;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
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
        return sections.stream()
                .sorted(Section::compareTo)
                .flatMap(Section::stations)
                .distinct()
                .collect(Collectors.toList());
    }

    public void removeSectionByStation(Station stationToRemove) {
        List<Station> stations = getStations();
        validateStationToRemove(stations, stationToRemove);

        if (isIndexStation(FIRST_INDEX, stationToRemove, stations)) {
            removeFirstSection(stationToRemove);
            return;
        }

        if (isIndexStation(stations.size() - 1, stationToRemove, stations)) {
            removeLastSection(stationToRemove);
            return;
        }

        removeBetweenSection(stationToRemove);
    }

    private void validateSection(List<Station> stations, Section sectionToAdd) {
        if (isContainsAllSectionStations(stations, sectionToAdd)) {
            throw new NotValidStationException("구간 추가할 역이 모두 노선에 포함되어 있습니다.");
        }

        if (isNotContainsAllSectionStations(stations, sectionToAdd)) {
            throw new NotValidStationException("구간 추가할 역 중 노선에 포함되는 역이 없습니다.");
        }
    }

    private boolean isContainsAllSectionStations(List<Station> stations, Section sectionToAdd) {
        return stations.contains(sectionToAdd.getUpStation()) && stations.contains(sectionToAdd.getDownStation());
    }

    private boolean isNotContainsAllSectionStations(List<Station> stations, Section sectionToAdd) {
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

    private void validateStationToRemove(List<Station> stations, Station stationToRemove) {
        if (!stations.contains(stationToRemove)) {
            // 에러 처리
        }
        if (sections.size() == 1) {
            // 에러 처리
        }
    }

    private boolean isIndexStation(int index, Station stationToRemove, List<Station> stations) {
        return stations.get(index).equals(stationToRemove);
    }

    private void removeFirstSection(Station stationToRemove) {
        findSectionByUpStation(stationToRemove).ifPresent(section ->
                section.removeLine(section.getLine()));
    }

    private void removeLastSection(Station stationToRemove) {
        findSectionByDownStation(stationToRemove).ifPresent(section ->
                section.removeLine(section.getLine()));
    }

    private void removeBetweenSection(Station stationToRemove) {
        Section upSection = findSectionByDownStation(stationToRemove).orElse(null);
        Section downSection = findSectionByUpStation(stationToRemove).orElse(null);

        if (upSection != null && downSection != null) {
            upSection.update(upSection.getUpStation(), downSection.getDownStation(), downSection.getDistanceValue());
        }

        downSection.removeLine(downSection.getLine());
    }
}
