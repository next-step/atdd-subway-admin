package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSection(Section addSection) {
        validDuplicateSection(addSection);
        validNotContainSection(addSection);

        Optional<Section> upSection = sections.stream().filter(addSection::isSameUpStation).findFirst();
        Optional<Section> downSection = sections.stream().filter(addSection::isSameDownStation).findFirst();

        upSection.ifPresent(section -> section.updateUpStation(addSection));
        downSection.ifPresent(section -> section.updateDownStation(addSection));

        sections.add(addSection);
    }

    private void validDuplicateSection(Section compareSection) {
        if (isContainsAllStation(compareSection)) {
            throw new IllegalArgumentException();
        }
    }

    private boolean isContainsAllStation(Section compareSection) {
        return sections.stream().map(Section::stations)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet())
            .containsAll(compareSection.stations());
    }

    private void validNotContainSection(Section compareSection) {
        if (sections.isEmpty()) {
            return;
        }

        if (getStations().stream().noneMatch(station -> compareSection.stations().contains(station))) {
            throw new IllegalArgumentException();
        }
    }

    public List<Station> getStations() {
        return sections.stream()
            .map(Section::stations)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    public int totalDistance() {
        return sections.stream().mapToInt(Section::getDistance).sum();
    }
}