package nextstep.subway.section.domain;

import com.google.common.collect.Lists;
import nextstep.subway.common.exception.DataRemoveException;
import nextstep.subway.common.message.ExceptionMessage;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int SECTION_MIN_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(Section section) {
        return new Sections(Lists.newArrayList(section));
    }

    public void add(Section section) {
        validateUniqueSection(section);
        validateIncludingUpStationOrDownStation(section);

        updateUpStation(section);
        sections.add(section);
    }

    public void remove(Station station) {
        validateIncludingStation(station);
        validateRemoveStation();

        removeStationBetweenUpStationAndDownStation(station);
        removeDownStation(station);
        removeUpStation(station);
    }

    private void validateIncludingStation(Station station) {
        if (!this.getStationsInOrder().contains(station)) {
            throw new DataRemoveException(ExceptionMessage.NOT_FOUND_STATION);
        }
    }

    private void validateRemoveStation() {
        if (sections.size() == SECTION_MIN_SIZE) {
            throw new DataRemoveException(ExceptionMessage.FAIL_TO_REMOVE_STATION_FROM_ONE_SECTION);
        }
    }

    private void removeStationBetweenUpStationAndDownStation(Station station) {
        Optional<Section> sectionIncludingTargetStation = sections.stream()
                .filter(it -> it.getDownStation().equals(station))
                .findAny();

        if (sectionIncludingTargetStation.isPresent()) {
            Section section = sectionIncludingTargetStation.get();
            remove(section);
            updateSection(station, section);
        }
    }

    private void updateSection(Station station, Section section) {
        Optional<Section> sectionIncludingRemovedStation = sections.stream()
                .filter(it -> it.getUpStation().equals(station))
                .findAny();

        if (sectionIncludingRemovedStation.isPresent()) {
            Section toBeUpdatedSection = sectionIncludingRemovedStation.get();
            toBeUpdatedSection.update(
                    section.getUpStation(),
                    section.getDistance().add(toBeUpdatedSection.getDistance())
            );
        }
    }

    private void removeDownStation(Station station) {
        sections.stream()
                .filter(it -> it.getDownStation().equals(station))
                .findAny()
                .ifPresent(this::remove);
    }

    private void removeUpStation(Station station) {
        sections.stream()
                .filter(it -> it.getUpStation().equals(station))
                .findAny()
                .ifPresent(this::remove);
    }

    private void remove(Section section) {
        section.removeFromLine();
        sections.remove(section);
    }

    private void validateUniqueSection(Section section) {
        if (sections.contains(section)) {
            throw new IllegalArgumentException(ExceptionMessage.ALREADY_ADDED_SECTION);
        }
    }

    private void validateIncludingUpStationOrDownStation(Section section) {
        List<Station> stations = this.getStationsInOrder();

        if (!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
            throw new IllegalArgumentException(ExceptionMessage.NOT_INCLUDE_UP_STATION_AND_DOWN_STATION);
        }
    }

    private void updateUpStation(Section section) {
        sections.stream()
                .filter(it -> it.getUpStation().equals(section.getUpStation()))
                .findAny()
                .ifPresent(it -> it.update(section.getDownStation(),
                        it.getDistance().subtract(section.getDistance())));
    }

    public List<Station> getStationsInOrder() {
        Station currentStation = findLineUpStation();
        List<Station> results = Lists.newArrayList(currentStation);

        Map<Station, Station> downStationByUpStation = sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));

        while (downStationByUpStation.get(currentStation) != null) {
            currentStation = downStationByUpStation.get(currentStation);
            results.add(currentStation);
        }

        return results;
    }

    private Station findLineUpStation() {
        List<Station> downStationsOfSection = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return sections.stream()
                .map(Section::getUpStation)
                .filter(upStation -> !downStationsOfSection.contains(upStation))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMessage.UP_STATION_NOT_EXIST_IN_LINE));
    }

    public Distance getTotalDistance() {
        Optional<Distance> distance = sections.stream()
                .map(Section::getDistance)
                .reduce(Distance::add);

        if (distance.isPresent()) {
            return distance.get();
        }

        throw new IllegalArgumentException(ExceptionMessage.INVALID_SECTION_DISTANCE);
    }
}
