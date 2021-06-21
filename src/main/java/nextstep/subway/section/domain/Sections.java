package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public void add(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }
        validateUpAndDownStation(newSection);
        changeStationInMiddleWhenEqualToUpStation(newSection);
        sections.add(newSection);
    }

    private void validateUpAndDownStation(Section newSection) {
        Station upStation = newSection.getUpStation();
        Station downStation = newSection.getDownStation();
        if (isStationInSections(upStation) && isStationInSections(downStation)) {
            throw new IllegalArgumentException("이미 존재하는 상행선 하행선 입니다.");
        }
        if (!isStationInSections(upStation) && !isStationInSections(downStation)) {
            throw new IllegalArgumentException("기존 구간에 상행역 하행역이 존재하지 않아 등록할 수 없습니다.");
        }
    }

    private boolean isStationInSections(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isStationInSection(station));
    }

    private void changeStationInMiddleWhenEqualToUpStation(Section newSection) {
        sections.stream()
                .filter(section -> section.equalUpStation(newSection))
                .findFirst()
                .ifPresent(section -> section.changeStationInMiddle(newSection));
    }

    public List<Station> getSortedStations() {
        Collections.sort(sections);
        List<Station> stationList = new ArrayList<>();
        stationList.add(getFirstStation());
        for (Section section : sections) {
            stationList.add(section.getDownStation());
        }
        return stationList;
    }

    private Station getFirstStation() {
        return sections.get(0).getUpStation();
    }

    public void removeSectionByStation(Station station) {
        Optional<Section> sectionWithPreviousStation = findSectionWithPreviousStation(station);
        Optional<Section> sectionWithNextStation = findSectionWithNextStation(station);

        validateStationInSections(sectionWithPreviousStation, sectionWithNextStation);
        removeWhenFirstStation(sectionWithPreviousStation, sectionWithNextStation);
        removeWhenLastStation(sectionWithPreviousStation, sectionWithNextStation);
        removeWhenMiddleStation(sectionWithPreviousStation, sectionWithNextStation);
    }

    private void removeWhenFirstStation(Optional<Section> sectionWithPreviousStation, Optional<Section> sectionWithNextStation) {
        if (!sectionWithPreviousStation.isPresent() && sectionWithNextStation.isPresent()) {
            sections.remove(sectionWithNextStation.get());
            sectionWithNextStation.get().removeLine();
        }
    }

    private void removeWhenLastStation(Optional<Section> sectionWithPreviousStation, Optional<Section> sectionWithNextStation) {
        if (sectionWithPreviousStation.isPresent() && !sectionWithNextStation.isPresent()) {
            sections.remove(sectionWithPreviousStation.get());
            sectionWithPreviousStation.get().removeLine();
        }
    }

    private void removeWhenMiddleStation(Optional<Section> sectionWithPreviousStation, Optional<Section> sectionWithNextStation) {
        if (sectionWithPreviousStation.isPresent() && sectionWithNextStation.isPresent()) {
            sectionWithPreviousStation.get().changeDownStation(sectionWithNextStation.get().getDownStation());
            sectionWithPreviousStation.get().calculateDistanceWhenRemove(sectionWithNextStation.get());
            sections.remove(sectionWithNextStation.get());
            sectionWithNextStation.get().removeLine();
        }
    }

    private void validateStationInSections(Optional<Section> sectionWithPreviousStation, Optional<Section> sectionWithNextStation) {
        if (!sectionWithPreviousStation.isPresent() && !sectionWithNextStation.isPresent()) {
            throw new IllegalArgumentException("존재하지 않는 역입니다.");
        }
    }

    private Optional<Section> findSectionWithPreviousStation(Station station) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst();
    }

    private Optional<Section> findSectionWithNextStation(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst();
    }
}
