package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.exception.BothStationAlreadyExistsException;
import nextstep.subway.exception.DistanceIsEqualOrGreaterException;
import nextstep.subway.exception.ResourceNotFoundException;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public boolean addSection(final Section addableSection) {
        checkBothStationAlreadyExists(addableSection);
        if (checkSectionIsBetween(addableSection)) {
            checkDistanceIsEqualOrGreaterThan(addableSection);
        }
        return sections.add(addableSection);
    }

    private void checkBothStationAlreadyExists(final Section addableSection) {
        final long matchedCount = getAllStations().stream()
                .filter(station ->
                        station == addableSection.getUpStation() ||
                                station == addableSection.getDownStation())
                .count();

        if (matchedCount == 2) {
            throw new BothStationAlreadyExistsException();
        }
    }

    private List<Station> getAllStations() {
        return sections.stream()
                .map(section ->
                        Arrays.asList(section.getUpStation(), section.getDownStation()))
                .flatMap(stations -> stations.stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean checkSectionIsBetween(final Section addableSection) {
        return findBetweenSection(addableSection).isPresent();
    }

    private Optional<Section> findBetweenSection(final Section addableSection) {
        return sections.stream()
                .filter(section ->
                        section.getUpStation().equals(addableSection.getUpStation()) ||
                                section.getDownStation().equals(addableSection.getDownStation()))
                .findFirst();
    }

    private void checkDistanceIsEqualOrGreaterThan(final Section addableSection) {
        final Section originalSection = findBetweenSection(addableSection).orElseThrow(
                () -> new ResourceNotFoundException(Section.class));
        if (originalSection.isDistanceEqualOrGreaterThan(addableSection)) {
            throw new DistanceIsEqualOrGreaterException();
        }
    }

    public List<Station> getSortedStations() {
        final List<Station> sortedStations = new ArrayList<>();
        final Station rootStation = searchRootStation();
        sortedStations.add(rootStation);

        Optional<Station> nextStation = findNextStation(rootStation);
        while (nextStation.isPresent()) {
            final Station next = nextStation.get();
            sortedStations.add(next);
            nextStation = findNextStation(next);
        }
        return sortedStations;
    }

    private Station searchRootStation() {
        final Set<Station> upStations = findUpStations();
        final Set<Station> downStations = findDownStations();
        return upStations.stream()
                .filter(upStation ->
                        !downStations.contains(upStation))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(Station.class));
    }

    private Set<Station> findUpStations() {
        return sections.stream()
                .map(
                        section -> section.getUpStation())
                .collect(
                        Collectors.toSet());
    }

    private Set<Station> findDownStations() {
        return sections.stream()
                .map(section ->
                        section.getDownStation())
                .collect(Collectors.toSet());
    }

    private Optional<Station> findNextStation(final Station station) {
        final Optional<Section> nextSection = sections.stream()
                .filter(section ->
                        section.isEqualToUpStation(station))
                .findFirst();
        return nextSection.map(section -> section.getDownStation());
    }
}
