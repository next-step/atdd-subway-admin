package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {
    public static final String EXISTS_SECTION_EXCEPTION_MESSAGE = "상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.";
    public static final String NOT_EXISTS_ALL_STATIONS_EXCEPTION_MESSAGE = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.";
    public static final String AT_LEAST_ONE_SECTION_EXCEPTION_MESSAGE = "구간은 최소 한개 이상이어야만 합니다.";
    public static final String NOT_EXISTS_STATION_EXCEPTION_MESSAGE = "해당 노선에 요청한 지하철역 ID를 가진 구간이 존재하지 않습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    @ReadOnlyProperty
    private final List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Station> getSortedStations() {
        List<Station> sortedStations = new ArrayList<>();
        Section firstSection = findFirstSection();
        firstSection.addStations(sortedStations);
        addNextStation(sortedStations, firstSection);
        return sortedStations;
    }

    private void addNextStation(List<Station> stations, Section previousSection) {
        Optional<Section> nextSection = findNextSection(previousSection);
        while (nextSection.isPresent()) {
            Section currentSection = nextSection.get();
            currentSection.addNextStation(stations);
            nextSection = findNextSection(currentSection);
        }
    }

    private Optional<Section> findNextSection(Section previousSection) {
        return sections.stream()
                .filter(section -> section.isNextSection(previousSection))
                .findFirst();
    }

    private Section findFirstSection() {
        Section firstSection = sections.get(0);
        Optional<Section> previousSection = findPreviousSection(firstSection);
        while (previousSection.isPresent()) {
            firstSection = previousSection.get();
            previousSection = findPreviousSection(firstSection);
        }
        return firstSection;
    }

    private Optional<Section> findPreviousSection(Section nextSection) {
        return sections.stream()
                .filter(section -> section.isPreviousSection(nextSection))
                .findFirst();
    }

    public void add(Section sectionToAdd) {
        checkValidationToAdd(sectionToAdd);
        for (Section section : sections) {
            section.addInsideOfSection(sectionToAdd);
        }
        sections.add(sectionToAdd);
    }

    private void checkValidationToAdd(Section sectionToAdd) {
        if (sections.isEmpty()) {
            return;
        }
        if (exists(sectionToAdd)) {
            throw new IllegalArgumentException(EXISTS_SECTION_EXCEPTION_MESSAGE);
        }
        if (notExists(sectionToAdd)) {
            throw new IllegalArgumentException(NOT_EXISTS_ALL_STATIONS_EXCEPTION_MESSAGE);
        }
    }

    private boolean notExists(Section sectionToAdd) {
        return sections.stream()
                .map(Section::toStations)
                .flatMap(Collection::stream)
                .distinct()
                .noneMatch(station -> sectionToAdd.isDownStationEqualsToStation(station) ||
                        sectionToAdd.isUpStationEqualsToStation(station));
    }

    private boolean exists(Section sectionToAdd) {
        return sections.stream()
                .anyMatch(section -> section.isUpStationEqualsToUpStationInSection(sectionToAdd)) &&
                sections.stream()
                        .anyMatch(section -> section.isDownStationEqualsToDownStationInSection(sectionToAdd));
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public Section remove(Station station) {
        checkValidationFromSingleSection();
        Section sectionToRemove = findSectionToRemove(station);
        this.sections.remove(sectionToRemove);
        return sectionToRemove;
    }

    private Section findSectionToRemove(Station station) {
        Optional<Section> upSection = this.sections.stream()
                .filter(section -> section.isDownStationEqualsToStation(station))
                .findFirst();
        Optional<Section> downSection = this.sections.stream()
                .filter(section -> section.isUpStationEqualsToStation(station))
                .findFirst();
        if (!upSection.isPresent() && !downSection.isPresent()) {
            throw new IllegalArgumentException(NOT_EXISTS_STATION_EXCEPTION_MESSAGE);
        }
        if (upSection.isPresent() && downSection.isPresent()) {
            downSection.get().removeUpSection(upSection.get());
        }
        return upSection.orElseGet(downSection::get);
    }

    private void checkValidationFromSingleSection() {
        if (sections.size() == 1) {
            throw new IllegalArgumentException(AT_LEAST_ONE_SECTION_EXCEPTION_MESSAGE);
        }
    }
}
