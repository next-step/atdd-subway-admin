package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    public static final String SECTIONS_CANNOT_BE_NULL = "구간목록은 NULL이 될수 없습니다.";
    public static final String CANNOT_FOUND_INDEX_OF_SECTION = "기준이 되는 구간을 찾을 수 없습니다.";
    public static final String SECTION_ALREADY_EXISTS = "이미 상행역과 하행역으로 연결되는 구간이 등록되어 있습니다.";
    public static final String THERE_IS_NO_STATION_INCLUDED_BETWEEN_UP_AND_DOWN_STATIONS = "상행역과 하행역 둘중 포함되는 역이 없습니다.";
    public static final int ONE = 1;
    public static final boolean EMPTY_UPSTATION = false;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        if (sections == null) {
            throw new IllegalArgumentException(SECTIONS_CANNOT_BE_NULL);
        }
        this.sections = sections;
    }

    public void add(Section section) {
        if (isPresent(section)) {
            throw new IllegalArgumentException(SECTION_ALREADY_EXISTS);
        }
        if (!addInOrder(section)) {
            throw new IllegalArgumentException(THERE_IS_NO_STATION_INCLUDED_BETWEEN_UP_AND_DOWN_STATIONS);
        }
    }

    private boolean addInOrder(Section section) {
        if (isBefore(section)) {
            return addLast(section);
        }
        Optional<Section> targetOptional = findTargetSectionForAdd(section);
        if (targetOptional.isPresent()) {
            return addBetween(targetOptional.get(), section);
        }
        if (isAfter(section)) {
            return addFirst(section);
        }
        return false;
    }

    private boolean isBefore(Section findSection) {
        if (sections.isEmpty()) {
            return true;
        }
        return sections.stream()
            .filter(section -> section.isBefore(findSection))
            .findFirst().isPresent();
    }

    private boolean isAfter(Section findSection) {
        if (sections.isEmpty()) {
            return true;
        }
        return sections.stream()
            .filter(section -> section.isAfter(findSection))
            .findFirst().isPresent();
    }

    private boolean addFirst(Section section) {
        sections.add(0, section);
        return true;
    }

    private boolean addLast(Section section) {
        sections.add(sections.size(), section);
        return true;
    }

    private boolean isPresent(Section section) {
        return sections.stream()
            .filter(s -> s.isEqualUpAndDownStation(section))
            .findFirst().isPresent();
    }

    private Optional<Section> findTargetSectionForAdd(Section findSection) {
        return sections.stream()
            .filter(section -> section.isBetween(findSection))
            .findFirst();
    }

    private boolean addBetween(Section targetSection, Section newSection) {
        targetSection.addIntoSection(newSection);
        sections.add(indexOf(targetSection), newSection);
        return true;
    }

    private int indexOf(Section section) {
        return IntStream.range(0, sections.size())
            .filter(i -> section.equals(sections.get(i)))
            .findFirst()
            .orElseThrow(() -> {
                throw new RuntimeException(CANNOT_FOUND_INDEX_OF_SECTION);
            }) + ONE;
    }

    public List<Station> findStationsInOrder() {
        Optional<Section> preSectionOptional = Optional.ofNullable(findFirstSection());
        List<Section> sortedSections = new ArrayList<>();
        while (preSectionOptional.isPresent()) {
            Section preSection = preSectionOptional.get();
            sortedSections.add(preSection);
            preSectionOptional = sections.stream()
                .filter(section -> section.isAfter(preSection))
                .findFirst();
        }
        List<Station> resultStations = getUpStations(sortedSections);
        if (!sortedSections.isEmpty()) {
            resultStations.add(getLastDownStation(sortedSections));
        }
        return resultStations;
    }

    private Section findFirstSection() {
        Map<Boolean, Section> map = new HashMap<>();
        sections.forEach(current -> {
            Optional<Section> optional = sections.stream()
                .filter(section -> section.isBefore(current))
                .findFirst();
            map.put(optional.isPresent(), current);
        });
        return map.get(EMPTY_UPSTATION);
    }

    private List<Station> getUpStations(List<Section> sections) {
        return sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
    }

    private Station getLastDownStation(List<Section> sections) {
        return sections.get(sections.size() - ONE).getDownStation();
    }

    public List<SectionResponse> toSectionResponses() {
        return sections.stream()
            .map(SectionResponse::of)
            .collect(Collectors.toList());
    }
}
