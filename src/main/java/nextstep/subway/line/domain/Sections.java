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
        Section firstSection = findFirstStation();
        addFirstSection(sortedStations, firstSection);
        addNextStation(sortedStations, firstSection.getDownStation());
        return sortedStations;
    }

    private void addFirstSection(List<Station> sortedStations, Section firstSection) {
        sortedStations.add(firstSection.getUpStation());
        sortedStations.add(firstSection.getDownStation());
    }

    private void addNextStation(List<Station> stations, Station downStation) {
        Optional<Section> nextSection = findNextSection(downStation);
        while(nextSection.isPresent()){
            Station currentStation = nextSection.get().getDownStation();
            stations.add(currentStation);
            nextSection = findNextSection(currentStation);
        }
    }

    private Optional<Section> findNextSection(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst();
    }

    private Section findFirstStation() {
        Section firstSection = sections.get(0);
        Optional<Section> beforeSection = findPreviousSection(firstSection.getUpStation());
        while(beforeSection.isPresent()){
            firstSection = beforeSection.get();
            beforeSection = findPreviousSection(firstSection.getUpStation());
        }
        return firstSection;
    }

    private Optional<Section> findPreviousSection(Station station) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(station))
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
                .noneMatch(station -> station.equals(sectionToAdd.getDownStation()) ||
                        station.equals(sectionToAdd.getUpStation()));
    }

    private boolean exists(Section sectionToAdd) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().equals(sectionToAdd.getUpStation())) &&
                sections.stream()
                        .anyMatch(section -> section.getDownStation().equals(sectionToAdd.getDownStation()));
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

}
