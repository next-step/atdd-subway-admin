package nextstep.subway.domain;

import nextstep.subway.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

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
        Section section = validate(newSection);
        reArrange(section, newSection);
        this.sections.add(newSection);
    }

    private void reArrange(final Section section, final Section newSection) {
        changeSection(section, newSection);
    }

    private void changeSection(final Section section, final Section newSection) {
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

    private Section validate(final Section newSection) {
        Section section = isPossibleToConnect(newSection);
        if (isIncludedBetweenStations(newSection, section)) {
            validateDistance(newSection, section);
        }
        return section;
    }

    private boolean isIncludedBetweenStations(final Section newSection, final Section section) {
        return isSameUpStation(section, newSection) || isSameDownStation(section, newSection);
    }

    private void validateDistance(final Section newSection, final Section section) {
        if (newSection.getDistance() >= section.getDistance()) {
            throw new IllegalArgumentException("상행역 또는 하행역이 동일한 경우, 기존 구간의 거리보다 짧아야 합니다.");
        }
    }

    private Section isPossibleToConnect(final Section newSection) {
        return sections.stream()
                .filter(s -> isInsertable(s, newSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("새 구간을 연결할 수 있는 구간이 없습니다."));
    }

    private boolean isInsertable(final Section section, final Section newSection) {
        Set<Station> copy = new HashSet<>(section.getStations());
        copy.addAll(newSection.getStations());
        return copy.size() == COUNT_TO_CREATE_SECTION_BETWEEN_STATIONS
                && !Objects.equals(newSection.getDownStation(), newSection.getUpStation());
    }

    public List<StationResponse> toResponse() {
        return sections.stream()
                .flatMap(o -> o.getStations().stream())
                .map(o -> new StationResponse(o.getId(), o.getName(), o.getCreatedDate(), o.getModifiedDate()))
                .distinct()
                .collect(Collectors.toList());
    }
}
