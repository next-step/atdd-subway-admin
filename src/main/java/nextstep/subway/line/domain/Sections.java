package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        Direction direction = isUpDirection(section) ? Direction.UP : Direction.DOWN;
        changeSection(section, direction);
    }

    private boolean isUpDirection(Section section) {
        return sections.stream().anyMatch(s -> s.getUpStation() == section.getUpStation());
    }

    private void changeSection(Section section, Direction direction) {
        sections.stream()
                .filter(s -> s.getStation(direction) == section.getStation(direction))
                .findFirst()
                .ifPresent(s -> {
                    int distance = s.getDistance() - section.getDistance();
                    Section moveSection = createByDirection(direction, section, s, distance);
                    moveSection.toLine(section.getLine());

                    int index = sections.indexOf(s);
                    sections.remove(s);

                    List<Section> asList = direction == Direction.UP ?
                        Arrays.asList(section, moveSection) : Arrays.asList(moveSection, section);
                    this.sections.addAll(index,asList);
                });
    }

    private Section createByDirection(Direction direction, Section newSection, Section oldSection, int distance) {
        if (direction == Direction.UP) {
            return new Section(newSection.getStation(Direction.DOWN), oldSection.getStation(Direction.DOWN), distance);
        }
        return new Section(oldSection.getStation(Direction.UP), newSection.getStation(Direction.UP), distance);

    }

    public List<Station> stations() {
        Set<Station> stations = new LinkedHashSet<>();
        for (Section section : sections) {
            stations.addAll(Arrays.asList(section.getUpStation(), section.getDownStation()));
        }
        return new ArrayList<>(stations);
    }
}
