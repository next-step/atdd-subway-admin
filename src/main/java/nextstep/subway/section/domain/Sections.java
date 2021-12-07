package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;
import org.springframework.data.repository.cdi.Eager;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", fetch = FetchType.EAGER)
    private final List<Section> sections = new ArrayList<>();

    @Transient
    private Stations stations;
    @Transient
    private Station firstStation;
    @Transient
    private Station lastStation;

    public Sections() {
        stations = new Stations();
    }

    public Sections(Section section) {
        stations = new Stations();
        addSection(0, section);
    }

    public Sections(List<Section> sections) {
        stations = new Stations();
        this.sections.addAll(sections);
    }

    public List<Section> getSections() {
        return sections;
    }

    public Section getSection(int index) {
        return sections.get(index);
    }

    public void addSection(int index, Section section) {
        this.sections.add(index, section);
    }

    public List<Station> getSortedStations() {
        findFirstLastStation();
        stations.addStation(firstStation);
        findNextSection(firstStation);
        return stations.getStations();
    }

    private void findNextSection(Station station) {
        for (Section section: sections) {
            if (station.isSameStation(section.getUpStation())) {
                Station nextStation = section.getDownStation();
                stations.addStation(nextStation);
                if (nextStation.isSameStation(lastStation)) {
                    return;
                }
                findNextSection(nextStation);
            }
        }
    }

    private void findFirstLastStation() {
        findFirstStation();
        findLastStation();
    }

    private void findFirstStation() {
        Set<Station> upStationIds = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toSet());

        Set<Station> downStationIds = sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toSet());

        upStationIds.removeAll(downStationIds);
        this.firstStation = upStationIds.stream().findFirst().get();
    }

    private void findLastStation() {
        Set<Station> upStationIds = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toSet());

        Set<Station> downStationIds = sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toSet());

        downStationIds.removeAll(upStationIds);
        this.lastStation = downStationIds.stream().findFirst().get();
    }
}
