package nextstep.subway.section.domain;

import nextstep.subway.section.exception.InvalidAddSectionException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    @OrderColumn
    private final List<Section> sections = new LinkedList<>();

    public void addInitSection(Section section) {
        sections.add(section);
    }

    public void addSection(Section section) {
        checkContainSection(section);
        checkContainAnyStations(section);

        addSectionInMatch(section);
    }

    public List<StationResponse> getStationResponses() {
        return getStations().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    private void addSectionInMatch(Section newSection) {
        // Add InSection
        // 기존 Section 상행 종점과 새로운 Section 상행 종점이 일치한 경우
        if (isExistCompare(section -> section.equalUpUpStation(newSection))) {
            Section oldSection = findSection(section -> section.equalUpUpStation(newSection));
            addSectionUpToUp(oldSection, newSection);
            return;
        }
        // 기존 Section 하행 종점과 새로운 Section 하행 종점이 일치한 경우
        if (isExistCompare(section -> section.equalDownDownStation(newSection))) {
            Section oldSection = findSection(section -> section.equalDownDownStation(newSection));
            addSectionDownToDown(oldSection, newSection);
            return;
        }

        // Add OutSection
        addOutSection(newSection);
    }

    private boolean isExistCompare(Predicate<Section> predicate) {
        return findSection(predicate) != null;
    }

    private Section findSection(Predicate<Section> predicate) {
        return sections.stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }

    private void addSectionUpToUp(Section oldSection, Section newSection) {
        checkDistance(oldSection, newSection);

        sections.add(sections.indexOf(oldSection), newSection);
        oldSection.updateUpStation(newSection.getDownStation(), oldSection.minusDistance(newSection));
    }

    private void addSectionDownToDown(Section oldSection, Section newSection) {
        checkDistance(oldSection, newSection);

        oldSection.updateDownStation(newSection.getUpStation(), oldSection.minusDistance(newSection));
        sections.add(sections.indexOf(oldSection) + 1, newSection);
    }

    private void addOutSection(Section newSection) {
        // 기존 Section 상행 종점과 새로운 Section 하행 종점이 일치한 경우 -> 새로운 역을 상행 종점으로..
        Section oldSection = findSection(section -> section.equalUpDownStation(newSection));
        if (oldSection != null) {
            sections.add(sections.indexOf(oldSection), newSection);
            return;
        }

        // 기존 Section 하행 종점과 새로운 Section 상행 종점이 일치한 경우 -> 새로운 역을 하행 종점으로..
        int index = sections.indexOf(findSection(section -> section.equalDownUpStation(newSection)));
        sections.add(index + 1, newSection);
    }

    private void checkContainSection(Section section) {
        List<Station> stations = getStations();
        List<Station> stationsOfNewSection = Arrays.asList(section.getUpStation(), section.getDownStation());
        if (stations.containsAll(stationsOfNewSection)) {
            throw new InvalidAddSectionException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
    }

    private void checkContainAnyStations(Section section) {
        List<Station> stations = getStations();
        if(!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
            throw new InvalidAddSectionException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
        }
    }

    private void checkDistance(Section oldSection, Section newSection) {
        if (newSection.isEqualOrMoreDistance(oldSection)) {
            throw new InvalidAddSectionException("기존 역 사이 길이보다 크거나 같으면 등록을 할 수가 없습니다.");
        }
    }

    private List<Station> getStations() {
        Set<Station> stations = new LinkedHashSet<>();
        sections.forEach(section -> {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        });

        return new ArrayList<>(stations);
    }
}
