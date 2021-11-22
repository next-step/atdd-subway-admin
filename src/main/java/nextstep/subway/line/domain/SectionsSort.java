package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SectionsSort {
    private final List<Section> sections;
    private final Map<Station, Station> sectionMap;
    private final List<Station> sortedStations = new ArrayList<>();

    private SectionsSort(List<Section> sections) {
        this.sections = sections;
        this.sectionMap = getStationMap();
    }

    public static SectionsSort of(List<Section> sections) {
        return new SectionsSort(sections);
    }

    public List<Station> sortUpToDown() {
        addDownStationByUpStationRecursive(getMaxTopStation());
        return Collections.unmodifiableList(sortedStations);
    }

    private Map<Station, Station> getStationMap() {
        return this.sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
    }

    private void addDownStationByUpStationRecursive(final Station downStation) {
        sortedStations.add(downStation);
        getNextUpStation(downStation)
                .ifPresent(this::addDownStationByUpStationRecursive);
    }

    private Optional<Station> getNextUpStation(final Station downStation) {
        if (sectionMap.containsKey(downStation)) {
            return Optional.of(sectionMap.get(downStation));
        }
        return Optional.empty();
    }

    private Station getMaxTopStation() {

        final List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return sections.stream()
                .map(Section::getUpStation)
                .filter(upStation -> !downStations.contains(upStation))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
