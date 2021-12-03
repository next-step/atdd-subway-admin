package nextstep.subway.line.domain;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    public static final int REMOVE_SECTION_MIN_SIZE = 1;
    public static final int NOT_BETWEEN_SECTION = 1;
    public static final int BETWEEN_SECTION = 2;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    public void add(Section addSection) {
        if (!sections.isEmpty()) {
            checkAddSection(addSection);
            addSectionBetweenSections(addSection);
        }
        sections.add(addSection);
    }

    public void remove(Station removeStation) {
        validateRemove();
        List<Section> findSections = findSections(removeStation);
        removeSectionNotBetweenSections(findSections);
        removeSectionBetweenSections(findSections);
    }

    public List<Station> getOrderedStations() {
        if (sections.isEmpty()) {
            return new ArrayList<>();
        }

        Station firstUpStation = findFirstUpStation();
        Station lastDownStation = findLastDownStation();

        return makeOrderedStations(firstUpStation, lastDownStation);
    }

    public List<Section> getOrderedSections() {
        if (sections.isEmpty()) {
            return sections;
        }

        Section firstSection = findFirstSection();
        Section lastSection = findLastSection();

        return makeOrderedSections(firstSection, lastSection);
    }

    private void checkAddSection(Section addSection) {
        validateExistsUpStationAndDownStation(addSection);
        validateNotExistsUpStationAndDownStation(addSection);
    }

    private void validateExistsUpStationAndDownStation(Section addSection) {
        if (isEqualsUpStation(addSection) && isEqualsDownStation(addSection)) {
            throw new BadRequestException("이미 등록된 구간입니다.");
        }
    }

    private void validateNotExistsUpStationAndDownStation(Section addSection) {
        if (isNotExistsStation(addSection.getUpStation()) && isNotExistsStation(addSection.getDownStation())) {
            throw new BadRequestException("연결될 구간이 없습니다.");
        }
    }

    private void addSectionBetweenSections(Section addSection) {
        if (isEqualsUpStation(addSection)) {
            sections.stream()
                    .filter(section -> section.isEqualsUpStation(addSection.getUpStation()))
                    .filter(section -> section.isDistanceGreaterThan(addSection))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("기존 역 사이 길이 보다 크거나 같으면 등록을 할 수 없습니다."))
                    .changeUpStationToAddSectionDownStation(addSection);
        }
    }

    private void validateRemove() {
        if (sections.size() == REMOVE_SECTION_MIN_SIZE) {
            throw new BadRequestException("구간을 제거할 수 없습니다.");
        }
    }

    private List<Section> findSections(Station removeStation) {
        return getOrderedSections().stream()
                .filter(section -> section.isEqualsUpStation(removeStation)
                        || section.isEqualsDownStation(removeStation))
                .collect(Collectors.toList());
    }

    private void removeSectionNotBetweenSections(List<Section> findSections) {
        if (findSections.size() == NOT_BETWEEN_SECTION) {
            sections.remove(findSections.get(0));
        }
    }

    private void removeSectionBetweenSections(List<Section> findSections) {
        if (findSections.size() == BETWEEN_SECTION) {
            Section upSection = findSections.get(0);
            Section downSection = findSections.get(1);
            upSection.changeDownStationToRemoveSectionDownStation(downSection);
            sections.remove(downSection);
        }
    }

    private boolean isEqualsUpStation(Section addSection) {
        return sections.stream()
                .anyMatch(section -> section.isEqualsUpStation(addSection.getUpStation()));
    }

    private boolean isEqualsDownStation(Section addSection) {
        return sections.stream()
                .anyMatch(section -> section.isEqualsDownStation(addSection.getDownStation()));
    }

    private boolean isNotExistsStation(Station addStation) {
        return makeStations().stream()
                .noneMatch(station -> station.equals(addStation));
    }

    private List<Station> makeStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Station> makeOrderedStations(Station firstStation, Station lastDownStation) {
        List<Station> orderedStations = new ArrayList<>();
        orderedStations.add(firstStation);

        Station nextStation = firstStation;
        while (!lastDownStation.equals(nextStation)) {
            nextStation = findNextStation(nextStation);
            orderedStations.add(nextStation);
        }
        return orderedStations;
    }

    private Station findNextStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualsUpStation(station))
                .map(Section::getDownStation)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("해당 지하철역이 존재하지않습니다."));
    }

    private Station findFirstUpStation() {
        List<Station> allUpStations = getAllUpStations();
        List<Station> allDownStations = getAllDownStations();
        allUpStations.removeAll(allDownStations);
        return allUpStations.get(0);
    }

    private Station findLastDownStation() {
        List<Station> allUpStations = getAllUpStations();
        List<Station> allDownStations = getAllDownStations();
        allDownStations.removeAll(allUpStations);
        return allDownStations.get(0);
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

    private List<Section> makeOrderedSections(Section firstSection, Section lastSection) {
        List<Section> orderedSections = new ArrayList<>();
        orderedSections.add(firstSection);

        Section nextSection = firstSection;

        while (!lastSection.equals(nextSection)) {
            nextSection = findNextSection(nextSection);
            orderedSections.add(nextSection);
        }
        return orderedSections;
    }

    private Section findNextSection(Section findSection) {
        return sections.stream()
                .filter(section -> section.isEqualsUpStation(findSection.getDownStation()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("해당 노선이 존재하지않습니다."));
    }

    private Section findLastSection() {
        Station lastDownStation = findLastDownStation();
        return sections.stream()
                .filter(section -> section.isEqualsDownStation(lastDownStation))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("하행역 종점을 찾을 수 없습니다."));
    }

    private Section findFirstSection() {
        Station firstUpStation = findFirstUpStation();
        return sections.stream()
                .filter(section -> section.isEqualsUpStation(firstUpStation))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("상행역 종점을 찾을 수 없습니다."));
    }
}
