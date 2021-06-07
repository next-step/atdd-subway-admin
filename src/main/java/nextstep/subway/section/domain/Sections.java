package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import nextstep.subway.station.domain.Station;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        validateSectionsSize(sections);
        this.sections = sections;
    }

    public List<Station> getSortedStations() {
        Section beginSection = getBeginSection();
        Map<String, Section> sections = getSectionsByUpStationName();
        String nextStationName = beginSection.getDownStationName();
        Set<Station> stations = new LinkedHashSet<>(beginSection.getStations());
        while (sections.containsKey(nextStationName)) {
            nextStationName = addNextStation(stations, sections.get(nextStationName));
        }
        return new ArrayList<>(stations);
    }

    private Section getBeginSection() {
        return this.sections.stream()
                .filter(section -> !getSectionsByDownStationName().containsKey(section.getUpStationName()))
                .findFirst().orElseThrow(() -> new NoSuchElementException("시작 구간을 찾을 수 없습니다."));
    }

    private String addNextStation(Set<Station> stations, Section nextSections) {
        Station nextStation = nextSections.getDownStation();
        stations.add(nextStation);
        return nextStation.getName();
    }

    private Map<String, Section> getSectionsByUpStationName() {
        return this.sections.stream()
                .collect(Collectors.toMap(section -> section.getUpStationName(), section -> section));
    }

    private Map<String, Section> getSectionsByDownStationName() {
        return this.sections.stream()
                .collect(Collectors.toMap(section -> section.getDownStationName(), section -> section));
    }

    private void validateSectionsSize(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("구간 목록은 하나 이상의 구간으로 구성되어야 합니다.");
        }
    }
}
