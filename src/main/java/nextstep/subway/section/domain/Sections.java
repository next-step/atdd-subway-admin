package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    public static final String SECTIONS_CANNOT_BE_NULL = "구간목록은 NULL이 될수 없습니다.";
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

    public boolean add(Section newSection) {
        if (!sections.isEmpty()) {
            validationForAdd(newSection);
        }
        changeSectionWhenUpStationMatch(newSection);
        changeSectionWhenDownStationMatch(newSection);
        return sections.add(newSection);
    }

    private void changeSectionWhenUpStationMatch(Section newSection) {
        sections.stream()
            .filter(section -> section.isEqualUpStation(newSection))
            .findFirst()
            .ifPresent(section -> {
                section.updateUpStationTo(newSection.getDownStation());
                section.minusDistance(newSection.getDistance());
            });
    }

    private void changeSectionWhenDownStationMatch(Section newSection) {
        sections.stream()
            .filter(section -> section.isEqualDownStation(newSection))
            .findFirst()
            .ifPresent(section -> {
                section.updateDownStationTo(newSection.getUpStation());
                section.minusDistance(newSection.getDistance());
            });
    }

    private void validationForAdd(Section section) {
        if (isPresent(section)) {
            throw new IllegalArgumentException(SECTION_ALREADY_EXISTS);
        }
        if (!isPresentAnyStation(section)) {
            throw new IllegalArgumentException(THERE_IS_NO_STATION_INCLUDED_BETWEEN_UP_AND_DOWN_STATIONS);
        }
    }

    private boolean isPresent(Section section) {
        return sections.stream()
            .filter(s -> s.isEqualAllStation(section))
            .findFirst().isPresent();
    }

    private boolean isPresentAnyStation(Section section) {
        return sections.stream()
            .filter(s -> s.isPresentAnyStation(section))
            .findFirst().isPresent();
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
