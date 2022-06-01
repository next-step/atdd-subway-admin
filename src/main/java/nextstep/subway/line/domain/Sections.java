package nextstep.subway.line.domain;

import static nextstep.subway.common.exception.ErrorMessage.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    private static final int MINIMUM_SECTIONS_SIZE = 2;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public void addSection(Section newSection) {
        if (sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }
        validateSection(newSection);
        sections.forEach(section -> section.update(newSection));
        sections.add(newSection);
    }

    private void validateSection(Section newSection) {
        if (hasUpStationAndDownStation(newSection)) {
            throw new IllegalArgumentException(ALREADY_REGISTERED_ERROR.getMessage());
        }

        if(hasNotUpStationAndDownStation(newSection)) {
            throw new IllegalArgumentException(NO_EXISTS_STATION_ADD_ERROR.getMessage());
        }
    }

    private boolean hasUpStationAndDownStation(Section newSection) {
        List<Station> stations = findAllStations();
        return stations.contains(newSection.getUpStation()) && stations.contains(newSection.getDownStation());
    }

    private boolean hasNotUpStationAndDownStation(Section newSection) {
        List<Station> stations = findAllStations();
        return !stations.contains(newSection.getUpStation()) && !stations.contains(newSection.getDownStation());
    }

    private List<Station> findAllStations() {
        return sections.stream()
                .map(Section::findStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return sections;
    }

    public void remove(Station station) {
        validateStation(station);
        Optional<Section> upStationOptional = findUpStation(station);
        Optional<Section> downStationOptional = findDownStation(station);

        if (upStationOptional.isPresent() && downStationOptional.isPresent()) {
            addRearrangedSection(downStationOptional.get(), upStationOptional.get());
        }

        upStationOptional.ifPresent(sections::remove);
        downStationOptional.ifPresent(sections::remove);
    }

    private void validateStation(Station station) {
        if (isLessThan(MINIMUM_SECTIONS_SIZE)) {
            throw new IllegalArgumentException(SECTIONS_SIZE_ERROR.getMessage());
        }
        if (hasNot(station)) {
            throw new IllegalArgumentException(NO_EXISTS_STATION_REMOVE_ERROR.getMessage());
        }
    }

    public boolean isLessThan(int size) {
        return sections.size() < size;
    }

    public boolean hasNot(Station station) {
        List<Station> stations = findAllStations();
        return !stations.contains(station);
    }

    private Optional<Section> findUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualUpStation(station))
                .findFirst();
    }

    private Optional<Section> findDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualDownStation(station))
                .findFirst();
    }

    private void addRearrangedSection(Section upSection, Section downSection) {
        sections.add(upSection.rearrange(downSection));
    }
}
