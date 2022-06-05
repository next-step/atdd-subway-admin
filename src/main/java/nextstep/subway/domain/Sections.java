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
import nextstep.subway.exception.BothStationNotExistsException;
import nextstep.subway.exception.DistanceIsEqualOrGreaterException;
import nextstep.subway.exception.ResourceNotFoundException;
import nextstep.subway.exception.SectionLessOrEqualThanOneException;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    private final static long BOTH_ALL_MATCHED_COUNT = 2;
    private final static long BOTH_NON_MATCHED_COUNT = 0;

    public boolean addSection(final Section addableSection) {
        checkBothStationExists(addableSection);
        if (checkSectionIsBetween(addableSection)) {
            checkDistanceIsEqualOrGreaterThan(addableSection);
            updateOriginSection(addableSection);
        }
        return sections.add(addableSection);
    }

    private void checkBothStationExists(final Section addableSection) {
        if (sections.isEmpty()) {
            return;
        }

        final long matchedCount = getAllStations().stream()
                .filter(station -> addableSection.isEqualToUpOrDownStation(station))
                .count();

        validateDuplicate(matchedCount);
        validateBothStationNotExists(matchedCount);
    }

    private void validateDuplicate(final long matchedCount) {
        if (matchedCount == BOTH_ALL_MATCHED_COUNT) {
            throw new BothStationAlreadyExistsException();
        }
    }

    private void validateBothStationNotExists(final long matchedCount) {
        if (matchedCount == BOTH_NON_MATCHED_COUNT) {
            throw new BothStationNotExistsException();
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
                .filter(section -> section.isEqualToUpOrDownStation(addableSection))
                .findFirst();
    }

    private void checkDistanceIsEqualOrGreaterThan(final Section addableSection) {
        final Section originalSection = findBetweenSection(addableSection).orElseThrow(
                () -> new ResourceNotFoundException(Section.class));
        if (originalSection.isDistanceEqualOrGreaterThan(addableSection)) {
            throw new DistanceIsEqualOrGreaterException();
        }
    }

    private void updateOriginSection(final Section addableSection) {
        whenUpStationIsEqual(addableSection);
        whenDownStationIsEqual(addableSection);
    }

    private void whenUpStationIsEqual(final Section addableSection) {
        sections.stream()
                .filter(section -> section.getUpStation().equals(addableSection.getUpStation()))
                .findFirst()
                .ifPresent(section -> section.updateUpStation(addableSection));
    }

    private void whenDownStationIsEqual(final Section addableSection) {
        sections.stream()
                .filter(section -> section.getDownStation().equals(addableSection.getDownStation()))
                .findFirst()
                .ifPresent(section -> section.updateDownStation(addableSection));
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

    public void removeSectionByStationId(final Long stationId) {
        checkSectionLessOrEqualThanOne();
    }

    private void checkSectionLessOrEqualThanOne() {
        if (sections.size() <= 1) {
            throw new SectionLessOrEqualThanOneException();
        }
    }
}
