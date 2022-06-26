package nextstep.subway.domain.line;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.domain.station.Station;

@Embeddable
public class Sections {

    private static final String EQUALS_SECTION_ERROR_MESSAGE = "동일 구간은 추가할 수 없습니다.";
    private static final String NOT_CONTAINS_ANY_STATION_ERROR_MESSAGE = "인근역과 접점이 없는 구간은 추가할 수 없습니다.";
    public static final String TOTAL_DISTANCE_OVER_RANGE_ERROR_MESSAGE = "구간의 길이는 노선의 총 길이보다 클 수 없습니다.";
    public static final String NOT_CONTAINS_TARGET_STATION_ERROR_MESSAGE = "삭제 대상역이 노선에 존재하지 않습니다.";
    public static int MINIMUM_REMOVE_SIZE = 1;
    public static final String CAN_NOT_REMOVE_SECTIONS_SIZE_ERROR_MESSAGE = String
        .format("구간의 길이가 %d 이하인 경우 삭제할 수 없습니다.", MINIMUM_REMOVE_SIZE);

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections addInitialSection(Section section) {
        List<Section> sections = new ArrayList<>();
        sections.add(section);
        return new Sections(sections);
    }

    public List<Section> getSections() {
        return sections;
    }

    private boolean isSameUpStation(Station upStation) {
        return getAllUpStations().contains(upStation);
    }

    private boolean isSameDownStation(Station downStation) {
        return getAllDownStations().contains(downStation);
    }

    public void add(Section section) {
        validateSection(section);
        if (isSameUpStation(section.getUpStation())) {
            findSectionByUpStation(section.getUpStation())
                .swapUpStationToTargetDownStation(section);
        }
        if (isSameDownStation(section.getDownStation())) {
            findSectionByDownStation(section.getDownStation())
                .swapDownStationToTargetUpStation(section);
        }
        sections.add(section);
    }

    private void validateSection(Section section) {
        validateEqualsSection(section);
        validateNotContainsAnyStation(section);
        validateDistance(section);
    }

    private void validateEqualsSection(Section section) {
        if (isSameUpStation(section.getUpStation()) && isSameDownStation(section.getDownStation())) {
            throw new IllegalArgumentException(EQUALS_SECTION_ERROR_MESSAGE);
        }
    }

    private void validateNotContainsAnyStation(Section section) {
        List<Station> allStations = sortedByFinalUpStations();
        if (!allStations.contains(section.getUpStation())
            && !allStations.contains(section.getDownStation())) {
            throw new IllegalArgumentException(NOT_CONTAINS_ANY_STATION_ERROR_MESSAGE);
        }
    }

    private void validateDistance(Section section) {
        if (section.getDistance() >= totalDistance()) {
            throw new IllegalArgumentException(TOTAL_DISTANCE_OVER_RANGE_ERROR_MESSAGE);
        }
    }

    public int totalDistance() {
        return sections.stream()
            .map(Section::getDistance)
            .mapToInt(value -> value)
            .sum();
    }

    public List<Station> sortedByFinalUpStations() {
        List<Station> sortedByFinalUpStations = new ArrayList<>();
        sortedByFinalUpStations.add(getFinalUpStation());

        for (int i = 0; i < sections.size(); i++) {
            Station nextNode = findSectionByUpStation(sortedByFinalUpStations.get(i)).getDownStation();
            sortedByFinalUpStations.add(nextNode);
        }
        return sortedByFinalUpStations;
    }

    private List<Station> getAllUpStations() {
        return sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
    }

    private List<Station> getAllDownStations() {
        return sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());
    }

    public Station getFinalUpStation() {
        List<Station> upStations = getAllUpStations();
        upStations.removeAll(getAllDownStations());
        return getStationByOptional(upStations);
    }

    public Station getFinalDownStation() {
        List<Station> downStations = getAllDownStations();
        downStations.removeAll(getAllUpStations());
        return getStationByOptional(downStations);
    }

    private Station getStationByOptional(List<Station> stations) {
        return stations.stream()
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    public Section findSectionByUpStation(Station station) {
        return sections.stream()
            .filter(it -> it.getUpStation().getName().equals(station.getName()))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    private Section findSectionByDownStation(Station station) {
        return sections.stream()
            .filter(it -> it.getDownStation().getName().equals(station.getName()))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    public Section getFinalUpSection() {
        return findSectionByUpStation(getFinalUpStation());
    }

    public Section getFinalDownSection() {
        return findSectionByDownStation(getFinalDownStation());
    }

    public void remove(Station station) {
        validateRemoveStation(station);
        if (getFinalUpStation().equals(station)) {
            sections.remove(getFinalUpSection());
            return;
        }

        if (getFinalDownStation().equals(station)) {
            sections.remove(getFinalDownSection());
            return;
        }

        Section updateTargetSection = findSectionByDownStation(station);
        Section removeTargetSection = findSectionByUpStation(station);
        updateTargetSection.swapDownStationToRemoveTargetDownStation(removeTargetSection);
        sections.remove(removeTargetSection);
    }

    private void validateRemoveStation(Station station) {
        validateSectionsSize();
        validateRemoveTargetStation(station);
    }

    private void validateSectionsSize() {
        if (sections.size() <= MINIMUM_REMOVE_SIZE) {
            throw new IllegalArgumentException(CAN_NOT_REMOVE_SECTIONS_SIZE_ERROR_MESSAGE);
        }
    }

    private void validateRemoveTargetStation(Station station) {
        if (!contains(station)) {
            throw new IllegalArgumentException(NOT_CONTAINS_TARGET_STATION_ERROR_MESSAGE);
        }
    }

    private boolean contains(Station station) {
        return sortedByFinalUpStations().contains(station);
    }
}
