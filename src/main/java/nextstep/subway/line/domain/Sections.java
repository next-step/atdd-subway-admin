package nextstep.subway.line.domain;

import nextstep.subway.exception.StationNotFoundException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public Sections() {
    }

    public List<Station> getStations() {
        return getStations(new ArrayList());
    }

    private List<Station> getStations(List<Station> stations) {
        Map<Station, Station> upToDown = getUpToDownStation();
        Station upStation = getUpStation();

        while (upStation != null) {
            stations.add(upStation);
            upStation = upToDown.get(upStation);
        }

        return stations;
    }

    private Map<Station, Station> getUpToDownStation() {
        return sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
    }

    private Station getUpStation() {
        List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return sections.stream()
                .map(Section::getUpStation)
                .filter(upStation -> !downStations.contains(upStation))
                .findFirst()
                .orElseThrow(StationNotFoundException::new);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public void add(Section section) {
        this.sections.add(section);
    }
}
