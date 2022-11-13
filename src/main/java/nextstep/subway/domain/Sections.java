package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.*;

import static java.util.Arrays.asList;

public class Sections {

    private static final int COUNT_TO_CREATE_SECTION_BETWEEN_STATIONS = 3;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(final Section... sections) {
        this.sections.addAll(asList(sections));
    }

    public void add(final Section newSection) {
        Section section = validateIfConnectableSectionWith(newSection);
        reArrangeSection(section, newSection);
        this.sections.add(newSection);
    }

    private void reArrangeSection(final Section section, final Section newSection) {
        if (isSameUpStation(section, newSection)) {
            section.changeUpStation(newSection.getDownStation());
            reArrangeDistance(section, newSection);
        } else if (isSameDownStation(section, newSection)) {
            section.changeDownStation(newSection.getUpStation());
            reArrangeDistance(section, newSection);
        }
    }

    private void reArrangeDistance(final Section section, final Section newSection) {
        section.changeDistance(section.getDistance() - newSection.getDistance());
    }

    private boolean isSameDownStation(final Section section, final Section newSection) {
        return section.getDownStation().equals(newSection.getDownStation());
    }

    private boolean isSameUpStation(final Section section, final Section newSection) {
        return section.getUpStation().equals(newSection.getUpStation());
    }

    private Section validateIfConnectableSectionWith(final Section newSection) {
        Section section = findConnectableSectionWith(newSection);
        if (isIncludedBetweenStations(newSection, section)) {
            section.validateDistance(newSection);
        }
        return section;
    }

    private boolean isIncludedBetweenStations(final Section newSection, final Section section) {
        return isSameUpStation(section, newSection) || isSameDownStation(section, newSection);
    }

    private Section findConnectableSectionWith(final Section newSection) {
        return sections.stream()
                .filter(section -> isInsertable(section, newSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("새 구간을 연결할 수 있는 구간이 없습니다."));
    }

    private boolean isInsertable(final Section section, final Section newSection) {
        Set<Station> copy = new HashSet<>(section.getStations());
        copy.addAll(newSection.getStations());
        return copy.size() == COUNT_TO_CREATE_SECTION_BETWEEN_STATIONS
                && !Objects.equals(newSection.getDownStation(), newSection.getUpStation());
    }

    public List<Section> getSections() {
        return sections;
    }
}
