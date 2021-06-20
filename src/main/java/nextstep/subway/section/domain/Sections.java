package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

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
                .filter(section -> section.equalsUpStation(newSection))
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
}
