package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    private static final int MIN_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections;

    public Sections() {
        this.sections = new ArrayList<>();
    }

    void add(Section section) {
        if (contains(section)) {
            return;
        }
        this.sections.add(section);
    }

    public List<Station> getSortedList() {
        return this.sections.stream()
            .sorted()
            .map(section -> section.getFromToStations())
            .flatMap(stations -> stations.stream())
            .distinct()
            .collect(Collectors.toList());
    }

    boolean contains(Section section) {
        return this.sections.contains(section);
    }

    void remove(Section section) {
        this.sections.remove(section);
    }

    boolean containsStation(Station station) {
        return getSortedList().contains(station);
    }

    int countSectionByStation(Station station) {
        return sections.stream()
            .filter(section -> section.isFromStation(station) || section.isToStation(station))
            .collect(Collectors.toList())
            .size();
    }

    boolean isMinSize() {
        if (sections.size() == MIN_SIZE) {
            return true;
        }
        return false;
    }

    Section findRemoveSection(Station station) {
        if (countSectionByStation(station) > MIN_SIZE) {
            return updateByRemoveStation(station);
        }

        return sections.stream()
            .filter(section -> section.isFromStation(station) || section.isToStation(station))
            .findFirst().orElseThrow(() -> new NotFoundException());
    }

    void updateByFromStation(Station stationToFind, Distance distance, Station fromStation) {
        Optional<Section> optionalSection = findByFromStation(stationToFind);
        optionalSection.ifPresent(section ->
            section.updateFromStation(distance, fromStation));
    }

    void updateByToStation(Station stationToFind, Distance distance, Station toStation) {
        Optional<Section> optionalSection = findByToStation(stationToFind);
        optionalSection.ifPresent(section ->
            section.updateToStation(distance, toStation));
    }

    Section updateByRemoveStation(Station station) {
        Section fromSection = findByFromStation(station)
            .orElseThrow(() -> new NotFoundException());
        Section toSection = findByToStation(station)
            .orElseThrow(() -> new NotFoundException());
        toSection.updateByRemoveSection(fromSection);
        return fromSection;
    }

    private Optional<Section> findByFromStation(Station station) {
        return sections.stream()
            .filter(section -> section.isFromStation(station))
            .findFirst();
    }

    private Optional<Section> findByToStation(Station station) {
        return sections.stream()
            .filter(section -> section.isToStation(station))
            .findFirst();
    }
}
