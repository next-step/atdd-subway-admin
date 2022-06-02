package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.exception.NotFoundException;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections valueOf(List<Section> sections) {
        return new Sections(sections);
    }

    public void addSection(Section section) {
        if (!isSectionsEmpty()) {
            validNotAddedSection(section);
            validContainsUpStationOrDownStation(section);
            update(section);
        }
        sections.add(section);
    }

    private boolean isSectionsEmpty() {
        return sections.isEmpty();
    }

    private void validNotAddedSection(Section section) {
        if (containsUpStationAndDownStation(section)) {
            throw new IllegalArgumentException("이미 등록된 구간 요청입니다.");
        }
    }

    private boolean containsUpStationAndDownStation(Section section) {
        return this.orderedStations().contains(section.upStation()) && this.orderedStations()
                .contains(section.downStation());
    }

    private void validContainsUpStationOrDownStation(Section section) {
        if (containsNoneOfUpStationAndDownStation(section)) {
            throw new IllegalArgumentException("등록을 위해 필요한 상행역과 하행역이 모두 등록되어 있지 않습니다.");
        }
    }

    private boolean containsNoneOfUpStationAndDownStation(Section section) {
        return !this.orderedStations().contains(section.upStation()) && !this.orderedStations()
                .contains(section.downStation());
    }

    private void update(Section newSection) {
        sections.forEach(section -> section.update(newSection));
    }

    public Distance distance() {
        return Distance.valueOf(sections.stream()
                .mapToInt(section -> section.distance().distance())
                .reduce(0, Integer::sum));
    }

    public List<Station> orderedStations() {
        Map<Station, Station> section = sections.stream()
                .collect(Collectors.toMap(Section::upStation, Section::downStation));
        Station currentStation = firstUpStation();
        Station lastDownStation = lastDownStation();
        List<Station> orderedStations = new ArrayList<>();
        while (section.containsKey(currentStation)) {
            orderedStations.add(currentStation);
            currentStation = section.get(currentStation);
        }
        orderedStations.add(lastDownStation);
        return orderedStations;
    }

    public void deleteSection(Station station) {
        if (isEqualToFirstUpStation(station)) {
            sections.remove(findSectionWithSameUpStation(station));
        }
        if (isEqualToLastDownStation(station)) {
            sections.remove(findSectionWithSameDownStation(station));
        }
        Section nextSection = findSectionWithSameUpStation(station);
        Section prevSection = findSectionWithSameDownStation(station);
        prevSection.updateDownStationByNextSection(nextSection);
        sections.remove(nextSection);
    }

    private boolean isEqualToFirstUpStation(Station station) {
        return station.equals(firstUpStation());
    }

    private Station firstUpStation() {
        Set<Station> upStations = sections.stream()
                .map(Section::upStation)
                .collect(Collectors.toSet());
        Set<Station> downStations = sections.stream()
                .map(Section::downStation)
                .collect(Collectors.toSet());
        return upStations.stream()
                .filter(upStation -> !downStations.contains(upStation))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("상행 종점역을 찾을 수 없습니다."));
    }

    private Section findSectionWithSameUpStation(Station station) {
        return sections.stream()
                .filter(section -> station.equals(section.upStation()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("일치하는 상행역이 없습니다."));
    }

    private boolean isEqualToLastDownStation(Station station) {
        return station.equals(lastDownStation());
    }

    private Station lastDownStation() {
        Set<Station> upStations = sections.stream()
                .map(Section::upStation)
                .collect(Collectors.toSet());
        Set<Station> downStations = sections.stream()
                .map(Section::downStation)
                .collect(Collectors.toSet());
        return downStations.stream()
                .filter(downStation -> !upStations.contains(downStation))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("하행 종점역을 찾을 수 없습니다."));
    }

    private Section findSectionWithSameDownStation(Station station) {
        return sections.stream()
                .filter(section -> station.equals(section.downStation()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("일치하는 하행역이 없습니다."));
    }
}