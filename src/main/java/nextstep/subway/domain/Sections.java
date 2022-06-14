package nextstep.subway.domain;

import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    private static final String ERROR_MESSAGE_ALL_ALREADY_REGISTERED = "상행 역과 하행 역이 이미 모두 등록되어 있어 구간을 추가할 수 없습니다.";
    private static final String ERROR_MESSAGE_NOT_CONTAINS = "상행 역과 하행 역 둘 중 하나도 포함되어있지 않아 구간을 추가할 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public void add(Section newSection) {
        if (sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }
        validateAddSection(newSection);
        sections.forEach(section -> section.update(newSection));
        sections.add(newSection);
    }

    private void validateAddSection(Section newSection) {
        if (hasUpStationAndDownStation(newSection)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_ALL_ALREADY_REGISTERED);
        }
        if (hasNotUpStationAndDownStation(newSection)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NOT_CONTAINS);
        }
    }

    private boolean hasUpStationAndDownStation(Section newSection) {
        List<Station> stations = findAllStations();
        return stations.contains(newSection.getUpStation()) && stations.contains(newSection.getDownStation());
    }

    private boolean hasNotUpStationAndDownStation(Section newSection) {
        List<Station> stations = findAllStations();
        return !stations.contains(newSection.getUpStation()) && !stations.contains(newSection.getDownStation());
    }

    public List<Station> findAllStations() {
        return sections.stream()
                .map(Section::getStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return sections;
    }
}
