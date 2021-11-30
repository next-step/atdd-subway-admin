package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static nextstep.subway.line.application.exception.SectionNotFoundException.error;

@Embeddable
public class Sections {

    public static final String NOT_CONNECTABLE = "구간을 연결할 상행역 또는 하행역이 존재해야 합니다.";
    public static final String NOT_FOUND_UP_TERMINUS = "상행 종점 구간을 찾을 수 없습니다.";
    public static final String BREAK_SECTION = "구간이 끊어져 있습니다.";

    @OneToMany(mappedBy = "line", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }
        addSection(newSection);
    }

    private void addSection(Section newSection) {
        Section section = sections.stream()
                .filter(s -> s.isNotDuplication(newSection))
                .findFirst()
                .map(s -> s.checkAddSection(newSection))
                .orElseThrow(() -> error(NOT_CONNECTABLE));

        sections.add(section);
    }

    private List<Section> sortSections() {
        List<Section> orderedSections = new LinkedList<>();
        Section orderedSection = findFirstSection();
        orderedSections.add(orderedSection);

        while (orderedSections.size() < sections.size()) {
            orderedSection = findNextStation(orderedSection.getDownStationName());
            orderedSections.add(orderedSection);
        }

        return Collections.unmodifiableList(orderedSections);
    }

    public Section findFirstSection() {
        List<String> downStationIds = getDownStationNames();

        return sections.stream()
                .filter(section -> !downStationIds.contains(section.getUpStationName()))
                .findFirst()
                .orElseThrow(() -> error(NOT_FOUND_UP_TERMINUS));
    }

    private Section findNextStation(String downStationNames) {
        return sections.stream()
                .filter(section -> Objects.equals(downStationNames, section.getUpStationName()))
                .findFirst()
                .orElseThrow(() -> error(BREAK_SECTION));
    }

    public List<String> getDownStationNames() {
        return Collections.unmodifiableList(sections.stream()
                .map(Section::getDownStation)
                .map(Station::getName)
                .collect(Collectors.toList()));
    }

    public List<Section> getSectionsInOrder() {
        return sortSections();
    }
}
