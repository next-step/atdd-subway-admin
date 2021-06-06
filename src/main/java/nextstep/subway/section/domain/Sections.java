package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import nextstep.subway.station.domain.Station;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        validateSectionsSize(sections);
        this.sections = sections;
    }

    public List<Station> getSortedStations() {
        return getSortedStationFromUpToDown(getBeginSection());
    }

    private Section getBeginSection() {
        return this.sections.stream()
                .filter(section -> !getSectionFilters().containsKey(section.getUpStation().getName()))
                .findFirst().orElseThrow(() -> new NoSuchElementException("시작 구간을 찾을 수 없습니다."));
    }

    private List<Station> getSortedStationFromUpToDown(Section beginSection) {
        List<Station> resultStations = new ArrayList<>();
        resultStations.addAll(beginSection.getStations());
        makeSortedStations(resultStations, getSectionsByUpStation(), beginSection.getDownStation().getName());
        return resultStations;
    }

    private void makeSortedStations(List<Station> resultStations, Map<String, Section> sections, String nextKey) {
        String nextStationName = nextKey;
        while (sections.containsKey(nextStationName)) {
            Station nextStation = sections.get(nextStationName).getDownStation();
            resultStations.add(nextStation);
            nextStationName = nextStation.getName();
        }
    }

    private Map<String, Section> getSectionsByUpStation() {
        return this.sections.stream()
                .collect(Collectors.toMap(section -> section.getUpStation().getName(), section -> section));
    }

    private Map<String, Section> getSectionFilters() {
        return this.sections.stream()
                .collect(Collectors.toMap(section -> section.getDownStation().getName(), section -> section));
    }

    private void validateSectionsSize(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("구간 목록은 하나 이상의 구간으로 구성되어야 합니다.");
        }
    }
}
