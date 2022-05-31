package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    private static final String ALREADY_REGISTERED_ERROR_MESSAGE = "상행 역과 하행 역이 이미 모두 등록되어 있어 구간 추가할 수 없습니다.";
    private static final String NO_EXISTS_ERROR_MESSAGE = "상행 역과 하행 역 둘 중 하나라도 등록되어 있지 않으면 구간 추가할 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public void addSection(Section newSection) {
        if (sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }
        validateSection(newSection);
        sections.forEach(section -> section.update(newSection));
        sections.add(newSection);
    }

    private void validateSection(Section newSection) {
        if (hasUpStationAndDownStation(newSection)) {
            throw new IllegalArgumentException(ALREADY_REGISTERED_ERROR_MESSAGE);
        }

        if(hasNotUpStationAndDownStation(newSection)) {
            throw new IllegalArgumentException(NO_EXISTS_ERROR_MESSAGE);
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

    private List<Station> findAllStations() {
        return sections.stream()
                .map(Section::findStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return sections;
    }
}
