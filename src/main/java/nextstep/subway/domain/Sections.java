package nextstep.subway.domain;

import nextstep.subway.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

import static nextstep.subway.common.ErrorMessage.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public void addBetweenSection(Section section) {
        validateSection(section);
        validateDuplicate(section);

        Section registeredSection = getRegisteredSection(section);
        sections.remove(registeredSection);
        sections.add(divideSection(section, registeredSection));
        sections.add(section);
    }

    private void validateDuplicate(Section section) {
        Set<Station> stations = new HashSet<>();
        for (Section registeredSection : sections) {
            stations.add(registeredSection.getUpStation());
            stations.add(registeredSection.getDownStation());
        }
        if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())) {
            throw new IllegalArgumentException(DUPLICATE_SECTION.getMessage());
        }
    }

    private void validateSection(Section section) {
        if (Objects.isNull(section)) {
            throw new IllegalArgumentException(SECTION_NOT_NULL.getMessage());
        }
    }

    private Section getRegisteredSection(Section section) {
        return sections.stream()
                .filter(registeredSection -> isContainStation(registeredSection, section))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(NOT_ALLOW_ADD_SECTION.getMessage()));
    }

    private Section divideSection(Section section, Section registeredSection) {
        Section dividedSection = createSection(section, registeredSection);
        dividedSection.addLine(section.getLine());
        return dividedSection;
    }

    private Section createSection(Section section, Section registeredSection) {
        Distance distance = registeredSection.getDistance().subtract(section.getDistance());

        if (Objects.equals(registeredSection.getUpStation(), section.getUpStation())) {
            return Section.of(section.getDownStation(), registeredSection.getDownStation(), distance);
        }
        return Section.of(registeredSection.getUpStation(), section.getUpStation(), distance);
    }

    private boolean isContainStation(Section registeredSection, Section section) {
        return Objects.equals(registeredSection.getUpStation(), section.getUpStation())
                || Objects.equals(registeredSection.getDownStation(), section.getDownStation());
    }

    public List<StationResponse> toResponse() {
        return sections.stream()
                .flatMap(section -> section.getStations().stream()).distinct()
                .map(station -> new StationResponse(station.getId(), station.getName(), station.getCreatedDate(), station.getModifiedDate()))
                .collect(Collectors.toList());
    }

    public List<Section> values() {
        return Collections.unmodifiableList(sections);
    }
}
