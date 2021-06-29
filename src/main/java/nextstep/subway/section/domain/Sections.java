package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(Section section) {
        sections.add(section);
    }

    public List<Station> stations() {
        return orderedSections().stream()
            .flatMap(section -> section.stations().stream())
            .distinct()
            .collect(Collectors.toList());
    }

    public void add(Section section) {
        validStations(section);
        splitSectionBy(section);
        sections.add(section);
    }

    private void validStations(Section section) {
        boolean isUpStationExist = section.isUpStationExist(stations());
        boolean isDownStationExist = section.isDownStationExist(stations());

        if (isUpStationExist && isDownStationExist) {
            throw new IllegalArgumentException("이미 추가한 구간입니다.");
        }

        if (!isUpStationExist && !isDownStationExist) {
            throw new IllegalArgumentException("구간을 추가할 수 없습니다.");
        }
    }

    private void splitSectionBy(Section section) {
        if (section.equalDownStation(headStation()) || section.equalUpStation(tailStation())) {
            return;
        }

        findOldSectionBy(section).splitBy(section);
    }

    private Station headStation() {
        return stations().get(0);
    }

    private Station tailStation() {
        return stations().get(stations().size() - 1);
    }

    private Section findHead() {
        Section section = sections.get(0);
        while (hasUpSection(section)) {
            section = findUpSection(section);
        }
        return section;
    }

    private List<Section> orderedSections() {
        List<Section> orderedSections = new ArrayList<>();
        Section section = findHead();
        orderedSections.add(section);
        while (hasDownSection(section)) {
            section = findDownSection(section);
            orderedSections.add(section);
        }
        return orderedSections;
    }

    private Section findOldSectionBy(Section section) {
        int index = IntStream.range(0, stations().size())
            .filter(i -> section.contains(stations().get(i)))
            .findFirst()
            .orElseThrow(EntityNotFoundException::new);

        if (section.isDownStationExist(stations())) {
            index -= 1;
        }

        return orderedSections().get(index);
    }

    private boolean hasUpSection(Section section) {
        return sections.stream().anyMatch(section::isDownSectionOf);
    }

    private boolean hasDownSection(Section section) {
        return sections.stream().anyMatch(section::isUpSectionOf);
    }

    private Section findUpSection(Section section) {
        return sections.stream()
            .filter(section::isDownSectionOf)
            .findFirst()
            .orElseThrow(EntityNotFoundException::new);
    }

    private Section findDownSection(Section section) {
        return sections.stream()
            .filter(section::isUpSectionOf)
            .findFirst()
            .orElseThrow(EntityNotFoundException::new);
    }

}
