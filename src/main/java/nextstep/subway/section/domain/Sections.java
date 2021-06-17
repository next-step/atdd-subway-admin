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
        if (sections.size() == 0) {
            sections.add(newSection);
            return;
        }
        validateExistUpAndDownStation(newSection);
        validateIncludeSameSection(newSection);
        addSectionInMiddleWhenEqualToUpStation(newSection);
        sections.add(newSection);
    }

    private void validateExistUpAndDownStation(Section newSection) {
        Set<Station> stationSet = new HashSet<>();
        for (Section section : sections) {
            stationSet.add(section.getUpStation());
            stationSet.add(section.getDownStation());
        }
        if (!(stationSet.contains(newSection.getUpStation()) || stationSet.contains(newSection.getDownStation()))) {
            throw new IllegalArgumentException("상행선 하행선 모두 등록되어있지 않습니다.");
        }
    }

    private void validateIncludeSameSection(Section newSection) {
        if (sections.contains(newSection)) {
            throw new IllegalArgumentException("동일한 구간은 등록할 수 없습니다.");
        }
    }

    private void addSectionInMiddleWhenEqualToUpStation(Section newSection) {
        sections.stream()
                .filter(section -> section.equalsUpStation(newSection))
                .findFirst()
                .ifPresent(section -> {
                    section.validateSectionDistance(newSection);
                    section.changeUpStation(newSection.getDownStation());
                    section.calculateDistance(newSection);
                });
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
