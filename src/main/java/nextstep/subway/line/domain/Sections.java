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
        Map<Station, Station> upToDowns = getUpToDownStation();
        Station upStation = getUpStation();

        while (upStation != null) {
            stations.add(upStation);
            upStation = upToDowns.get(upStation);
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
        sections.add(section);
    }

    public void updateUpStation(Station upStation, Station downStation, int distance) {
        sections.stream()
                .filter(section -> section.isUpStation(upStation))
                .findFirst()
                .ifPresent(section -> section.updateUpStation(downStation, distance));
    }

    public void updateDownStation(Station upStation, Station downStation, int distance) {
        sections.stream()
                .filter(section -> section.isDownStation(downStation))
                .findFirst()
                .ifPresent(section -> section.updateDownStation(upStation, distance));
    }

    public void removeSection(Station station) {
        Optional<Section> upStationSection = sections.stream()
                .filter(section -> section.isUpStation(station))
                .findAny();

        Optional<Section> downStationSection = sections.stream()
                .filter(section -> section.isDownStation(station))
                .findAny();

        if (upStationSection.isPresent()) {
            sections.remove(upStationSection.get());
        }

        if (downStationSection.isPresent()) {
            sections.remove(downStationSection.get());
        }
    }
}
