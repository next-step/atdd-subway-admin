package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

@Embeddable
public class Sections {

    private static final int REMOVABLE_SIZE = 2;
    private static final String NOT_LINKABLE_SECTION = "둘중 하나의 역이 등록이 되어 있어야 합니다.";
    private static final String SECTION_DUPLICATION_ERROR = "상, 하행 지하철역이 같은 구간 이미 등록 되어 있습니다.";
    private static final String UNDER_SECTIONS_SIZE_ERROR = "구간 적어 지하철역을 삭제 할 수 없습니다.";
    private static final String NO_ADDED_STATION_ERROR = "삭제 하려는 역이 존재 하지 않습니다.";
    private static final String NO_PREV_SECTION_ERROR = "이전 구간이 없습니다.";
    private static final String NO_NEXT_SECTION_ERROR = "다음 구간이 없습니다.";
    private static final String NO_LAST_SECTION_ERROR = "마지막 섹션이 없습니다.";
    private static final String NO_FIRST_SECTION_ERROR = "첫번째 섹션이 없습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections newInstance() {
        return new Sections();
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public List<StationResponse> getAllStations() {
        return findAllStations().stream().
                map(station -> StationResponse.of(station.getId(), station.getName(), station.getCreatedDate(),
                        station.getModifiedDate())).
                collect(Collectors.toList());
    }

    private Set<Station> findAllStations() {
        return sections.stream().
                flatMap(s -> s.getBothStations().stream()).
                collect(Collectors.toSet());
    }

    public void add(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }
        validateLinkableSection(newSection);
        validateDuplicateSection(newSection);
        sections.forEach(s -> s.modifySectionFor(newSection));
        sections.add(newSection);
    }

    private void validateDuplicateSection(Section section) {
        if (sections.stream().anyMatch(s -> s.isSameBothStation(section))) {
            throw new IllegalArgumentException(SECTION_DUPLICATION_ERROR);
        }
    }

    private void validateLinkableSection(Section section) {
        if (isNotAddedStation(section.getDownStation())
                && isNotAddedStation(section.getUpStation())) {
            throw new IllegalArgumentException(NOT_LINKABLE_SECTION);
        }
    }

    private boolean isNotAddedStation(Station station) {
        return !findAllStations().contains(station);
    }

    public void delete(Station station) {
        validateSectionSize();
        validateContainStation(station);
        Section firstSection = getFirstSection();
        Section lastSection = getLastSection();
        if (firstSection.getUpStation().equals(station)) {
            this.sections.remove(firstSection);
            return;
        }

        if (lastSection.getDownStation().equals(station)) {
            this.sections.remove(lastSection);
            return;
        }

        deleteMiddleSection(station);
    }

    private void validateContainStation(Station station) {
        if (isNotAddedStation(station)) {
            throw new IllegalArgumentException(NO_ADDED_STATION_ERROR);
        }
    }

    private void validateSectionSize() {
        if (sections.size() < REMOVABLE_SIZE) {
            throw new IllegalArgumentException(UNDER_SECTIONS_SIZE_ERROR);
        }
    }

    private void deleteMiddleSection(Station station) {
        Section prevSection = getPrevSection(station);
        Section nextSection = getNextSection(station);

        Section mergedSection = mergeSections(prevSection, nextSection);
        sections.add(mergedSection);
        sections.remove(prevSection);
        sections.remove(nextSection);
    }

    private Section mergeSections(Section prevSection, Section nextSection) {
        return prevSection.merge(nextSection);
    }

    private Section getPrevSection(Station station) {
        return this.sections.stream().
                filter(section -> section.getDownStation().equals(station)).
                findFirst().
                orElseThrow(() -> new NoSuchElementException(NO_PREV_SECTION_ERROR));
    }

    private Section getNextSection(Station station) {
        return this.sections.stream().
                filter(section -> section.getUpStation().equals(station)).
                findFirst().
                orElseThrow(() -> new NoSuchElementException(NO_NEXT_SECTION_ERROR));
    }

    private Section getLastSection() {
        List<Station> upStations = getUpStations();
        return sections.stream().filter(section -> !upStations.contains(section.getDownStation())).
                findAny().orElseThrow(() -> new NoSuchElementException(NO_LAST_SECTION_ERROR));
    }

    private List<Station> getUpStations() {
        return sections.stream().
                map(Section::getUpStation).
                collect(Collectors.toList());
    }

    private Section getFirstSection() {
        List<Station> downStations = getDownStations();
        return sections.stream().filter(section -> !downStations.contains(section.getUpStation())).
                findAny().orElseThrow(() -> new NoSuchElementException(NO_FIRST_SECTION_ERROR));
    }

    private List<Station> getDownStations() {
        return sections.stream().
                map(Section::getDownStation).
                collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
