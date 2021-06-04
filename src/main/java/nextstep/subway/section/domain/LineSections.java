package nextstep.subway.section.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

import static java.util.stream.Collectors.toMap;

@Embeddable
public class LineSections implements Serializable {

    private static final long serialVersionUID = -4483053178441994936L;
    private static final String MESSAGE_NOT_FOUND_UPSTATION = "상행역을 찾을 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        sections.add(section);
    }

    public List<Station> toStations() {
        Station startStation = getStartStation();
        return linkToLastStation(startStation);
    }

    private Station getStartStation() {

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

    private List<Station> linkToLastStation(Station startStation) {

        Map<Station, Station> stationRelationship =
            sections.stream()
                    .collect(toMap(Section::getUpStation,
                                   Section::getDownStation));

        List<Station> stations = new ArrayList<>();
        stations.add(startStation);

        Station nextStation = stationRelationship.get(startStation);
        while (nextStation != null) {
            stations.add(nextStation);
            nextStation = stationRelationship.get(nextStation);
        }

        return Collections.unmodifiableList(stations);
    }
}
