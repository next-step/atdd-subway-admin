package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new LinkedList<>();

    public boolean isEmpty() {
        return sections.size() == 0;
    }

    public boolean isNotEmpty() {
        return isEmpty() == false;
    }

    public void add(final Section newSection) {
        if (isNotEmpty()) {
            validation(newSection);
            overrideIfExistsUpStation(newSection);
            overrideIfExistsDownStation(newSection);
        }

        sections.add(newSection);
    }

    private List<Station> unSortedStations() {
        Set<Station> stationLink = new LinkedHashSet();

        for (Section section : sections) {
            stationLink.add(section.getUpStation());
            stationLink.add(section.getDownStation());
        }

        return new ArrayList<>(stationLink);
    }

    public List<Station> sortedStations() {
        List<Section> sections = sortedSections();

        List<Station> stations = new ArrayList<>();

        stations.add(sections.get(0).getUpStation());
        stations.addAll(sections.stream().map(Section::getDownStation).collect(Collectors.toList()));

        return stations;
    }

    public List<Section> sortedSections() {
        List<Section> sections = new ArrayList<>();

        concat(sections, new ArrayList<>(this.sections));

        return sections;
    }

    private void concat(final List<Section> sections, final List<Section> tempSections) {
        Section firstSection = firstSection(tempSections);
        tempSections.remove(firstSection);

        sections.add(firstSection);

        if (tempSections.size() > 0) {
            concat(sections, tempSections);
        }
    }

    private Section firstSection(List<Section> sections) {
        return sections.stream()
            .filter(this::isBeforeNoneMatch)
            .findFirst()
            .orElse(sections.get(0));
    }

    private Section lastSection(List<Section> sections) {
        return sections.stream()
            .filter(this::isAfterNoneMatch)
            .findFirst()
            .orElse(sections.get(0));
    }

    public Section firstSection() {
        return firstSection(this.sections);
    }

    public Section lastSection() {
        return lastSection(this.sections);
    }

    public Station firstStation() {
        return firstSection().getUpStation();
    }

    public Station lastStation() {
        return lastSection().getDownStation();
    }

    private boolean isBeforeNoneMatch(Section section) {
        return sections.stream().noneMatch(x -> x.isBefore(section));
    }

    private boolean isAfterNoneMatch(Section section) {
        return sections.stream().noneMatch(x -> x.isAfter(section));
    }

    public int totalDistance() {
        return sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }

    private void validation(final Section newSection) {
        List<Station> stations = unSortedStations();

        boolean isExistsUpStation = stations.contains(newSection.getUpStation());
        boolean isExistsDownStation = stations.contains(newSection.getDownStation());

        if (isExistsUpStation && isExistsDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음");
        }

        if (!isExistsUpStation && !isExistsDownStation) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음");
        }
    }

    private void overrideIfExistsUpStation(final Section newSection) {
        sections.stream()
                .filter(section -> newSection.hasSameUpStation(section))
                .findFirst()
                .ifPresent(section -> section.overrideUpStation(newSection));
    }

    private void overrideIfExistsDownStation(final Section newSection) {
        sections.stream()
                .filter(section -> newSection.hasSameDownStation(section))
                .findFirst()
                .ifPresent(section -> section.overrideDownStation(newSection));
    }

    public String format() {
        return sortedStations().stream().map(Station::getName).collect(Collectors.joining("-"));
    }
}
