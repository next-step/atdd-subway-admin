package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public static Sections empty() {
        return new Sections();
    }

    public boolean addSection(Section section) {
        return this.sections.add(section);
    }

    public List<Station> getStations() {
        Map<Station, Station> stationPath = makeStationPath();

        Optional<Station> upperMost = findUpperMost(stationPath);

        if (!upperMost.isPresent()) {
            return Collections.emptyList();
        }

        List<Station> stations = makeStations(stationPath, upperMost.get());

        return Collections.unmodifiableList(stations);
    }

    public void update(Section added) {
        validateSection(added);
        updateIfDownStationEquals(added);
        updateIfUpStationEquals(added);
        addSection(added);
    }

    private void validateSection(Section added) {
        List<Station> stations = getStations();

        boolean containsDownStation = stations.contains(added.getDownStation());
        boolean containsUpStation = stations.contains(added.getUpStation());

        if (containsDownStation && containsUpStation) {
            throw new IllegalArgumentException("이미 모두 구간에 포함되어 있습니다.");
        }

        if (!containsDownStation && !containsUpStation) {
            throw new IllegalArgumentException("모두 구간에 포함되어 있지 않습니다.");
        }
    }

    private void updateIfDownStationEquals(Section added) {
        Optional<Section> optionalDownSection = this.findSectionByDownStation(added.getDownStation());

        if (optionalDownSection.isPresent()) {
            Section origin = optionalDownSection.get();
            origin.updateDown(added);
        }
    }

    private Optional<Section> findSectionByDownStation(Station downStation) {
        return this.sections
            .stream()
            .filter(section -> section.hasDownStation(downStation))
            .findFirst();
    }

    private void updateIfUpStationEquals(Section added) {
        Optional<Section> optionalUpSection = this.findSectionByUpStation(added.getUpStation());

        if (optionalUpSection.isPresent()) {
            Section origin = optionalUpSection.get();
            origin.updateUp(added);
        }
    }

    private Optional<Section> findSectionByUpStation(Station upStation) {
        return this.sections
            .stream()
            .filter(section -> section.hasUpStation(upStation))
            .findFirst();
    }

    private Map<Station, Station> makeStationPath() {
        return sections.stream()
            .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
    }

    private Optional<Station> findUpperMost(Map<Station, Station> stationPath) {
        return stationPath.keySet()
            .stream()
            .filter(upStation -> !stationPath.containsValue(upStation))
            .findFirst();

    }

    private List<Station> makeStations(Map<Station, Station> stationPath, Station upperMost) {
        List<Station> stations = new ArrayList<>();

        Station station = upperMost;
        while (station != null) {
            stations.add(station);
            station = stationPath.get(station);
        }

        return stations;
    }
}
