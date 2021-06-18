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
            addValidation(newSection);
            overrideIfExistsUpStation(newSection);
            overrideIfExistsDownStation(newSection);
        }

        sections.add(newSection);
    }

    public boolean containsStation(final Station station) {
        return sections.stream()
                .anyMatch(section -> section.contains(station));
    }

    public void remove(final Station deleteStation) {
        deleteValidation(deleteStation);
        delete(deleteStation);
    }

    private void delete(final Station deleteStation) {
        List<Section> deleteSections = sections.stream()
                .filter(section -> section.contains(deleteStation))
                .collect(Collectors.toList());

        deleteSections.stream()
                .forEach(sections::remove);

        if (deleteSections.size() > 1) {
            arrange(firstSection(deleteSections), lastSection(deleteSections));
        }
    }

    private void deleteValidation(final Station deleteStation) {
        if (sections.size() == 1) {
            throw new IllegalArgumentException("구간의 최소 갯수는 1개입니다. 더이상 삭제할수 없습니다.");
        }

        if (containsStation(deleteStation) == false) {
            throw new IllegalArgumentException("존재하지 않는 역은 삭제할수 없습니다.");
        }
    }

    private List<Station> unSortedStations() {
        Set<Station> stationLink = new LinkedHashSet<>();

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

    private void arrange(final Section firstSection, final Section lastSection) {
        Section section = Section.builder()
            .upStation(firstSection.getUpStation())
            .downStation(lastSection.getDownStation())
            .distance(firstSection.getDistance() + lastSection.getDistance())
            .build();

        section.registerLine(firstSection.getLine());

        sections.add(section);
    }

    private Section firstSection(List<Section> sections) {
        return sections.stream()
            .filter(section -> isBeforeNoneMatch(sections, section))
            .findFirst()
            .orElse(sections.get(0));
    }

    private Section lastSection(List<Section> sections) {
        return sections.stream()
            .filter(section -> isAfterNoneMatch(sections, section))
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

    private boolean isBeforeNoneMatch(List<Section> sections, Section section) {
        return sections.stream().noneMatch(x -> x.isBefore(section));
    }

    private boolean isAfterNoneMatch(List<Section> sections, Section section) {
        return sections.stream().noneMatch(x -> x.isAfter(section));
    }

    public int totalDistance() {
        return sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }

    private void addValidation(final Section newSection) {
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
