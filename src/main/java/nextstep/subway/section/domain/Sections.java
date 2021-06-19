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
                            sections.add(section);
                            sections.add(Section.of(section.getDownStation(), oldSection.getDownStation(), oldSection.getDistance() - section.getDistance()));
                            sections.remove(oldSection);
                            return;
                        }
                );
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
    }

    private void validateDistance(int oldDistance, int newDistance) {
        if (oldDistance <= newDistance) {
            throw new IncorrectSectionException("길이가 잘못된 구간입니다.");
        }
    }

    //해당단계에서는 하나의 섹션에서 상행 종점, 하행 종점만 있으므로 유일한 섹션의 상행과 하행 순으로 출력한다.
    public Stations getStations() {
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

}
