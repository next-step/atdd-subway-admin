package nextstep.subway.section.domain;

import nextstep.subway.exception.IncorrectSectionException;
import nextstep.subway.line.dto.Stations;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {
    private static final int MINIMUM_SIZE = 1;
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new LinkedList<>();

    public Sections() {
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public void addInMiddle(Section section) {
        this.sections.stream()
                .filter(oldSection -> section.getUpStation().equals(oldSection.getUpStation()))
                .findFirst()
                .ifPresent(
                        oldSection -> {
                            validateDistance(oldSection.getDistance(), section.getDistance());
                            oldSection.changeStation(section.getDownStation(), oldSection.getDownStation(), oldSection.getDistance() - section.getDistance());
                            sections.add(sections.indexOf(oldSection), section);
                            return;
                        }
                );
        reindexing();
    }

    public void addOnTop(Section section) {
        this.sections.stream()
                .filter(oldSection -> section.getDownStation().equals(oldSection.getUpStation()))
                .findFirst()
                .ifPresent(
                        oldSection -> {
                            sections.add(0,section);
                        }
                );
        reindexing();
    }

    public void addBelow(Section section) {
        this.sections.stream()
                .filter(oldSection -> section.getUpStation().equals(oldSection.getDownStation()))
                .findFirst()
                .ifPresent(
                        oldSection -> {
                            sections.add(section);
                        }
                );
        reindexing();
    }

    private void validateDistance(int oldDistance, int newDistance) {
        if (oldDistance <= newDistance) {
            throw new IncorrectSectionException("길이가 잘못된 구간입니다.");
        }
    }


    public void Deletable() {
        if (sections.size() <= MINIMUM_SIZE) {
            throw new IncorrectSectionException("삭제할 수 있는 구간이 없습니다");
        }
    }

    public void deleteMiddleSectionBy(Station station) {
        Section upSection = getSectionByDown(station);
        Section downSection = getSectionByUp(station);
        upSection.changeStation(upSection.getUpStation(), downSection.getDownStation(), upSection.getDistance()+downSection.getDistance());
        sections.remove(downSection);
        reindexing();
    }

    public void deleteFirstSectionBy(Station station) {
        Section upSection = getSectionByUp(station);
        sections.remove(upSection);
        reindexing();
    }

    public void deleteLastSectionBy(Station station) {
        Section downSection = getSectionByDown(station);
        sections.remove(downSection);
    }

    private void reindexing() {
        sections.stream().forEach(section -> section.setSectionIndex(sections.indexOf(section)));
    }

    public Stations getStations() {
        sort();
        Stations stations = new Stations();
        stations.add(sections.get(0).getUpStation());
        for (Section section : sections) {
            stations.add(section.getDownStation());
        }
        return stations;
    }

    public Stations getUpStations() {
        Stations stations = new Stations();
        for (Section section : sections) {
            stations.add(section.getUpStation());
        }
        return stations;
    }

    public Stations getDownStations() {
        Stations stations = new Stations();
        for (Section section : sections) {
            stations.add(section.getDownStation());
        }
        return stations;
    }

    private Section getSectionByDown(Station station) {
        return sections.stream().filter(
                section -> section.getDownStation().equals(station)
        ).findFirst().get();
    }

    private Section getSectionByUp(Station station) {
        return sections.stream().filter(
                section -> section.getUpStation().equals(station)
        ).findFirst().get();
    }

    public void sort() {
        Collections.sort(sections);
    }
}
