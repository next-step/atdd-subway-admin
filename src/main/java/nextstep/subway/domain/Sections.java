package nextstep.subway.domain;

import nextstep.subway.exception.DuplicateSectionException;
import nextstep.subway.exception.InvalidSizeSectionException;
import nextstep.subway.exception.NoConnectedSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {
    private static final String NO_CONNECTED_SECTION_MESSAGE = "연결되는 구간이 아닙니다.";
    private static final String DUPLICATE_SECTION_EXCEPTION = "이미 노선에 등록되어 있는 구간입니다.";
    private static final String INVALID_SIZE_SECTION_EXCEPTION = "기존 구간에 등록할 수 없는 길이입니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public List<Station> getStationList() {
        Set<Station> stations = new HashSet<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return new ArrayList<>(stations);
    }

    public void add(Section section) {
        if (!sections.isEmpty()) {
            validate(section);
        }
        this.sections.add(section);
    }

    private void validate(Section section) {
        validateConnectedSection(section);
        validateDistinctSection(section);
        validateSectionSize(section);
    }

    private void validateSectionSize(Section section) {
        int targetDistance = section.getDistance();
        Section sectionByUpStation = findSectionByStation(section.getUpStation());
        Section sectionByDownStation = findSectionByStation(section.getDownStation());

        boolean upStationResult = isValidSize(targetDistance, sectionByUpStation);
        boolean downStationResult = isValidSize(targetDistance, sectionByDownStation);

        if (!upStationResult || !downStationResult) {
            throw new InvalidSizeSectionException(INVALID_SIZE_SECTION_EXCEPTION);
        }
    }

    private boolean isValidSize(int target, Section section) {
        boolean result = true;
        if (section != null) {
            int actual = section.getDistance();
            result = compareDistance(target, actual);
        }

        return result;
    }

    private boolean compareDistance(int target, int actual) {
        return actual > target;
    }

    private void validateDistinctSection(Section section) {
        Section sectionByUpStation = findSectionByStation(section.getUpStation());
        Section sectionByDownStation = findSectionByStation(section.getDownStation());

        if (sectionByUpStation != null && sectionByDownStation != null) {
            throw new DuplicateSectionException(DUPLICATE_SECTION_EXCEPTION);
        }
    }

    private void validateConnectedSection(Section section) {
        Section sectionByUpStation = findSectionByStation(section.getUpStation());
        Section sectionByDownStation = findSectionByStation(section.getDownStation());

        if (sectionByUpStation == null && sectionByDownStation == null) {
            throw new NoConnectedSectionException(NO_CONNECTED_SECTION_MESSAGE);
        }
    }

    private Section findSectionByStation(Station station) {
        return sections.stream()
                .filter(it -> it.hasSameNameStation(station))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
