package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SortStations {
    private final List<Section> sections;
    private final Map<Station, Station> sectionMap;
    private final List<Station> sortedStations = new ArrayList<>();

    private SortStations(List<Section> sections) {
        this.sections = sections;
        this.sectionMap = getStationMap();
    }

    private Map<Station, Station> getStationMap() {
        return this.sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
    }

    public static SortStations of(List<Section> sections) {
        return new SortStations(sections);
    }

    public List<Station> sortUpToDown() {
        final Station maxTopStation = getMaxTopStation();
        addDownStationByUpStationRecursive(maxTopStation);
        return Collections.unmodifiableList(sortedStations);
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
}
