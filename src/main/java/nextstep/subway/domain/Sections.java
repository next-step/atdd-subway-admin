package nextstep.subway.domain;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    private static final int MIN_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> values = new ArrayList<>();

    protected Sections() {
    }

    public void addSection(Section section) {
        if (values.isEmpty()) {
            values.add(section);
            return;
        }

        validateSection(section);

        findNextSection(section)
            .ifPresent(it -> it.updateUpStation(section.getDownStation(), section.getDistance()));

        findPrevSection(section)
            .ifPresent(it -> it.updateDownStation(section.getUpStation(), section.getDistance()));

        values.add(section);
    }

    private void validateSection(Section section) {
        if (containsAllStationsOf(section)) {
            throw new DuplicatedSectionException();
        }
        if (!values.isEmpty() && !containsAnyStationsOf(section)) {
            throw new ConnectedStationNotPresentException();
        }
    }

    private boolean containsAllStationsOf(Section section) {
        return containsUpStationOf(section)
            && containsDownStationOf(section);
    }

    private boolean containsAnyStationsOf(Section section) {
        return containsUpStationOf(section)
            || containsDownStationOf(section);
    }

    private boolean containsUpStationOf(Section section) {
        return containsStation(section.getUpStation());
    }

    private boolean containsDownStationOf(Section section) {
        return containsStation(section.getDownStation());
    }

    private Optional<Section> findNextSection(Section section) {
        return values.stream()
            .filter(section::equalUpStation)
            .findFirst();
    }

    private Optional<Section> findPrevSection(Section section) {
        return values.stream()
            .filter(section::equalDownStation)
            .findFirst();
    }

    private boolean containsStation(Station station) {
        return getUnorderedStations().contains(station);
    }

    private Set<Station> getUnorderedStations() {
        if (values.isEmpty()) {
            return Collections.emptySet();
        }
        final Set<Station> result = new HashSet<>();
        for (Section section : values) {
            result.add(section.getUpStation());
            result.add(section.getDownStation());
        }
        return result;
    }

    public List<Station> getStations() {
        if (values.isEmpty()) {
            return Collections.emptyList();
        }
        return getOrderedStations();
    }

    private List<Station> getOrderedStations() {
        List<Station> result = new ArrayList<>();

        // Find first station
        Station station = findUpStation();
        result.add(station);

        // Find rest
        Optional<Section> nextSection = findNextSection(station);
        while (nextSection.isPresent()) {
            station = nextSection
                .map(Section::getDownStation)
                .orElseThrow(NoSuchElementException::new);
            result.add(station);
            nextSection = findNextSection(station);
        }
        return Collections.unmodifiableList(result);
    }

    private Station findUpStation() {
        Station station = values.get(0).getUpStation();

        Optional<Section> prevSection = findPrevSection(station);
        while (prevSection.isPresent()) {
            station = prevSection
                .map(Section::getUpStation)
                .orElseThrow(NoSuchElementException::new);
            prevSection = findPrevSection(station);
        }
        return station;
    }

    private Optional<Section> findPrevSection(Station station) {
        return values.stream()
            .filter(section -> section.equalDownStation(station))
            .findFirst();
    }

    private Optional<Section> findNextSection(Station station) {
        return values.stream()
            .filter(section -> section.equalUpStation(station))
            .findFirst();
    }

    public void removeLineStation(Line line, Station station) {
        validateRemainingSectionsSize();
        validateStationIsPresent(station);

        final Optional<Section> optionalPrevSection = findPrevSection(station);
        final Optional<Section> optionalNextSection = findNextSection(station);

        optionalPrevSection.map(prevSection -> optionalNextSection.map(nextSection ->
            values.add(createSection(line, prevSection, nextSection))));
        optionalPrevSection.map(values::remove);
        optionalNextSection.map(values::remove);
    }

    private void validateRemainingSectionsSize() {
        if (values.size() <= MIN_SIZE) {
            throw new CannotRemoveSectionException();
        }
    }

    private void validateStationIsPresent(Station station) {
        if (!containsStation(station)) {
            throw new CannotRemoveSectionException();
        }
    }

    private Section createSection(Line line, Section prevSection, Section nextSection) {
        return new Section(
            line,
            prevSection.getUpStation(),
            nextSection.getDownStation(),
            prevSection.addDistance(nextSection));
    }

}
