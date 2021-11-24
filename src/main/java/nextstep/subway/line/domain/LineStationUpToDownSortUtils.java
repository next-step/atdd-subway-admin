package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LineStationUpToDownSortUtils {

    private LineStationUpToDownSortUtils() {
    }

    public static List<Station> sort(final List<Section> sections) {
        final List<Station> sortedStations = new ArrayList<>();
        addDownStationByUpStationRecursive(getStationMap(sections), getMaxTopStation(sections), sortedStations);
        return Collections.unmodifiableList(sortedStations);
    }

    private static Map<Station, Station> getStationMap(final List<Section> sections) {
        return sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
    }

    private static Station getMaxTopStation(final List<Section> sections) {
        final List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return sections.stream()
                .map(Section::getUpStation)
                .filter(upStation -> !downStations.contains(upStation))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private static void addDownStationByUpStationRecursive(final Map<Station, Station> sectionMap, final Station downStation, final List<Station> sortedStations) {
        sortedStations.add(downStation);
        getNextUpStation(sectionMap, downStation)
                .ifPresent(upStation -> addDownStationByUpStationRecursive(sectionMap, upStation, sortedStations));
    }

    private static Optional<Station> getNextUpStation(final Map<Station, Station> sectionMap, final Station downStation) {
        if (sectionMap.containsKey(downStation)) {
            return Optional.of(sectionMap.get(downStation));
        }
        return Optional.empty();
    }
}
