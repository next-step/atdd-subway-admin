package nextstep.subway.section.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.station.domain.Station;

public class Sections {

    public static final String AT_LEAST_ONE_SECTION_IS_REQUIRED = "1개 이상의 구간이 입력되어야 합니다.";
    private LinkedList<Section> sections;

    public Sections(List<Section> sections) {
        if (sections == null && sections.size() == 0) {
            throw new IllegalArgumentException(AT_LEAST_ONE_SECTION_IS_REQUIRED);
        }
        initSortSections(sections);
    }

    private void initSortSections(List<Section> sections) {
        this.sections = new LinkedList<>();
        for (Section section : sections) {
            addSection(section);
        }
    }

    private void addSection(Section section) {
        if (isBefore(section)) {
            sections.addFirst(section);
        }
        if (isAfter(section)) {
            sections.addLast(section);
        }
    }

    public boolean isBefore(Section findSection) {
        if (sections.size() == 0) {
            return true;
        }
        return sections.stream()
            .filter(section -> section.isBefore(findSection))
            .count() > 0;
    }

    public boolean isAfter(Section findSection) {
        if (sections.size() == 0) {
            return true;
        }
        return sections.stream()
            .filter(section -> section.isAfter(findSection))
            .count() > 0;
    }

    public List<Station> findSortedStations() {
        List<Station> stations = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
        if (sections.size() > 0) {
            stations.add(sections.getLast().getDownStation());
        }
        return stations;
    }
}
