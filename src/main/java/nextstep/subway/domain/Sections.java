package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.exception.SubwayExceptionMessage;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(final Section section) {
        validateDuplicate(section);
        sections.stream()
                .filter(origin -> origin.intersects(section))
                .findFirst()
                .ifPresent(origin -> origin.rearrange(section));

        sections.add(section);
    }

    private void validateDuplicate(final Section section) {
        if (hasSection(section)) {
            throw new SubwayException(SubwayExceptionMessage.DUPLICATE_SECTION);
        }
    }

    private boolean hasSection(final Section section) {
        return sections.stream().anyMatch(origin -> origin.equalsStations(section));
    }

    public Set<Station> getStationsOrderBy() {
        final Set<Station> stationSet = new LinkedHashSet<>();
        Station station = findFirstStation();
        stationSet.add(station);

        while (stationSet.size() <= sections.size()) {
            final Section nowSection = findSectionByUpStation(station);
            stationSet.add(nowSection.getDownStation());
            station = nowSection.getDownStation();
        }

        return stationSet;
    }

    private Section findSectionByUpStation(final Station station) {
        return sections.stream()
                .filter(section -> section.hasUpStation(station))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private Station findFirstStation() {
        return getStations().stream()
                .filter(this::noneHasDownStation)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    private boolean noneHasDownStation(final Station station) {
        return sections.stream().noneMatch(section -> section.hasDownStation(station));
    }

    private Set<Station> getStations() {
        return sections.stream()
                .flatMap(section -> section.getStations().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sections)) {
            return false;
        }
        final Sections that = (Sections) o;
        return Objects.equals(sections, that.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }

    @Override
    public String toString() {
        return "Sections{" +
                "sections=" + sections +
                '}';
    }
}
