package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
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
        sections.stream()
                .filter(oldSection -> section.getUpStation() == oldSection.getUpStation())
				.findFirst()
                .ifPresent(oldSection -> {
					Section moveSection = new Section(section.getDownStation(), oldSection.getDownStation(),oldSection.getDistance() - section.getDistance());
					moveSection.toLine(section.getLine());
                    int index = sections.indexOf(oldSection);
                    sections.remove(oldSection);
                    sections.add(index, moveSection);
				});
        int index = findIndexByObj(Direction.UP, section.getDownStation());
        sections.add(index, section);
    }

    private int findIndexByObj(Direction direction, Station find) {
        Section section = sections.stream()
            .filter(oldSection -> oldSection.getStation(direction) == find)
            .findFirst()
            .orElse(null);

        if (Objects.isNull(section)) {
            return 0;
        }
        return sections.indexOf(section);
    }

    public List<Station> stations() {
        Set<Station> stations = new LinkedHashSet<>();
        for (Section section : sections) {
            stations.addAll(Arrays.asList(section.getUpStation(), section.getDownStation()));
        }
        return new ArrayList<>(stations);
    }
}
