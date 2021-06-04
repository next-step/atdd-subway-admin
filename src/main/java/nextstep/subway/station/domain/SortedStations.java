package nextstep.subway.station.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import nextstep.subway.section.domain.LineSections;
import nextstep.subway.section.domain.Section;

import static java.util.stream.Collectors.toMap;

public class SortedStations implements Stations {

    private static final String MESSAGE_NOT_FOUND_UPSTATION = "상행역을 찾을 수 없습니다.";

    private List<Station> stations = new ArrayList<>();

    public SortedStations(LineSections lineSections) {
        List<Section> sections = lineSections.getSections();

        Station startStation = getStartStation(sections);
        linkToLastStation(startStation, sections);

        stations = Collections.unmodifiableList(stations);
    }

    private Station getStartStation(List<Section> sections) {

        Map<Station, Station> stationRelationship =
            sections.stream()
                    .collect(toMap(Section::getDownStation,
                                   Section::getUpStation));

        Entry<Station, Station> startEntry =
            stationRelationship.entrySet()
                               .stream()
                               .filter(entry -> !stationRelationship.containsKey(entry.getValue()))
                               .findAny()
                               .orElseThrow(
                                   () -> new IllegalStateException(MESSAGE_NOT_FOUND_UPSTATION));

        return startEntry.getValue();
    }

    private void linkToLastStation(Station startStation, List<Section> sections) {

        Map<Station, Station> stationRelationship =
            sections.stream()
                    .collect(toMap(Section::getUpStation,
                                   Section::getDownStation));

        stations.add(startStation);

        Station nextStation = stationRelationship.get(startStation);
        while (nextStation != null) {
            stations.add(nextStation);
            nextStation = stationRelationship.get(nextStation);
        }
    }

    @Override
    public List<Station> getStations() {
        return stations;
    }
}
