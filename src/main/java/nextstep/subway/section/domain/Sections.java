package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final String ALREADY_EXIST_UP_DOWN_STATION = "추가할 상/하행역이 이미 등록되어 있습니다.";
    private static final String NOT_FOUND_SORTED_NEXT_STATION = "정렬할 다음 역을 찾을 수 없습니다.";
    private static final String NOT_FOUND_EDGE_UP_SECTION = "상행 종점을 찾을 수 없습니다.";
    private static final String NOT_FOUND_EDGE_DOWN_SECTION = "하행 종점을 찾을 수 없습니다.";
    private static final String NOT_FOUND_UP_SECTION = "상행 구간을 찾을 수 없습니다.";
    private static final String NOT_FOUND_DOWN_SECTION = "하행 구간을 찾을 수 없습니다.";
    private static final String NOT_CONTAINS_CONNECT_SECTION = "기존 구간과 연결된 역을 찾을 수 없습니다.";
    private static final String NOT_REMOVED_ONLY_ONE_SECTION = "구간이 하나인 노선에서 역을 제거할 수 없습니다.";
    private static final String DOES_NOT_CONTAIN_STATION = "노선에 등록되어 있지 않은 역은 제거할 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    protected Sections() {
        this.sections = new ArrayList<>();
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections empty() {
        return new Sections();
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public void add(Section section) {
        validateContainsUpDownStation(section);
        if (addableSection(section)) {
            relocateConnectSection(section);
        }
        this.sections.add(section);
    }

    public void removeByStation(Station removeStation) {
        if (removableStation(removeStation)) {
            Section removeSection = findRemoveSection(removeStation);
            this.sections.remove(removeSection);
        }
    }

    public int size() {
        return this.sections.size();
    }

    public List<Section> get() {
        return this.sections;
    }

    public boolean containsStation(Station station) {
        return sortedLineStations().contains(station);
    }

    public int indexOfStation(Station station) {
        return sortedLineStations().indexOf(station);
    }

    public List<StationResponse> allStationResponses() {
        return sortedLineStations().toStationResponses();
    }

    private void validateContainsUpDownStation(Section section) {
        if (this.sections.isEmpty()) return;
        if (containsStation(section.getUpStation()) && containsStation(section.getDownStation())) {
            throw new IllegalArgumentException(ALREADY_EXIST_UP_DOWN_STATION);
        }
    }

    private LineStations sortedLineStations() {
        List<Station> sortedAllStations = new ArrayList<>();
        sortedAllStations.add(findEdgeUpSection().getUpStation());
        for (int i = 0; i < this.sections.size(); i++) {
            Station nextStation = findSectionByUpStation(sortedAllStations.get(i))
                    .map(Section::getDownStation)
                    .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_SORTED_NEXT_STATION));
            sortedAllStations.add(nextStation);
        }
        return LineStations.from(sortedAllStations);
    }

    private boolean addableSection(Section addedSection) {
        return !this.sections.isEmpty() && !isEdgeSection(addedSection);
    }

    private boolean removableStation(Station removeStation) {
        if (this.sections.size() == 1) {
            throw new IllegalArgumentException(NOT_REMOVED_ONLY_ONE_SECTION);
        }
        if (!containsStation(removeStation)) {
            throw new IllegalArgumentException(DOES_NOT_CONTAIN_STATION);
        }
        return !this.sections.isEmpty();
    }

    private boolean isEdgeSection(Section section) {
        return isEdgeUpSection(section) || isEdgeDownSection(section);
    }

    private boolean isEdgeUpSection(Section section) {
        return isEdgeUpSectionByStation(section.getDownStation());
    }

    private boolean isEdgeDownSection(Section section) {
        return isEdgeDownSectionByStation(section.getUpStation());
    }

    private boolean isEdgeStation(Station station) {
        return isEdgeUpSectionByStation(station) || isEdgeDownSectionByStation(station);
    }

    private boolean isEdgeUpSectionByStation(Station station) {
        return findEdgeUpSection().getUpStation().equals(station);
    }

    private boolean isEdgeDownSectionByStation(Station station) {
        return findEdgeDownSection().getDownStation().equals(station);
    }

    private Section findRemoveSection(Station removeStation) {
        if (isEdgeStation(removeStation)) {
            return findEdgeSectionByStation(removeStation);
        }

        Section upSection = findUpSectionByStation(removeStation);
        Section downSection = findDownSectionByStation(removeStation);
        relocateConnectSectionByRemovedSection(upSection, downSection);
        return downSection;
    }

    private Section findEdgeSectionByStation(Station station) {
        if (isEdgeUpSectionByStation(station)) {
            return findEdgeUpSection();
        }
        return findEdgeDownSection();
    }

    private Section findEdgeUpSection() {
        List<Station> downStations = this.sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        return this.sections.stream()
                .filter(section -> !section.isIncludedUpStation(downStations))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_EDGE_UP_SECTION));
    }

    private Section findEdgeDownSection() {
        List<Station> upStations = this.sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        return this.sections.stream()
                .filter(section -> !section.isIncludedDownStation(upStations))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_EDGE_DOWN_SECTION));
    }

    private void relocateConnectSectionByRemovedSection(Section connectSection, Section removedSection) {
        connectSection.updateDownStation(removedSection.getDownStation());
        connectSection.increaseDistance(removedSection.getDistance());
    }

    private void relocateConnectSection(Section addedSection) {
        Section connectSection = findConnectSection(addedSection);
        relocateUpSection(connectSection, addedSection);
        relocateDownSection(connectSection, addedSection);
    }

    private void relocateUpSection(Section connectSection, Section addedSection) {
        if (connectSection.isEqualsDownStation(addedSection.getDownStation())) {
            connectSection.updateDownStation(addedSection.getUpStation());
            connectSection.decreaseDistance(addedSection.getDistance());
        }
    }

    private void relocateDownSection(Section connectSection, Section addedSection) {
        if (connectSection.isEqualsUpStation(addedSection.getUpStation())) {
            connectSection.updateUpStation(addedSection.getDownStation());
            connectSection.decreaseDistance(addedSection.getDistance());
        }
    }

    private Section findUpSectionByStation(Station station) {
        return findSectionByDownStation(station).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_UP_SECTION));
    }

    private Section findDownSectionByStation(Station station) {
        return findSectionByUpStation(station).orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_DOWN_SECTION));
    }

    private Section findConnectSection(Section section) {
        return findSectionByUpStation(section.getUpStation()).orElseGet(() -> findSectionByDownStation(section.getDownStation())
                .orElseThrow(() -> new IllegalArgumentException(NOT_CONTAINS_CONNECT_SECTION)));
    }

    private Optional<Section> findSectionByUpStation(Station upStation) {
        return this.sections.stream()
                .filter(section -> section.isEqualsUpStation(upStation))
                .findFirst();
    }

    private Optional<Section> findSectionByDownStation(Station downStation) {
        return this.sections.stream()
                .filter(section -> section.isEqualsDownStation(downStation))
                .findFirst();
    }
}
