package nextstep.subway.station.domain;

import java.util.Collections;
import java.util.List;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SortedSection;

import static java.util.stream.Collectors.toList;

public class SortedStations {

    private final List<Station> stations;

    public SortedStations(SortedSection sortedSection) {

        List<Section> sections = sortedSection.getSections();
        List<Station> sortedStations = sections.stream()
                                               .map(Section::getUpStation)
                                               .collect(toList());

        sortedStations.add(getLastStation(sections));
        this.stations = Collections.unmodifiableList(sortedStations);
    }

    private Station getLastStation(List<Section> sections) {
        Section section = sections.get(sections.size() - 1);
        return section.getDownStation();
    }

    public List<Station> getStations() {
        return stations;
    }
}
