package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.StationNotFoundException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if (sections.contains(section)) {
            return;
        }
        insertion(section);
        sections.add(section);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Section section = getHeadSection();
        stations.add(section.getUpStation());
        stations.add(section.getDownStation());
        while ((section = getNextSection(section)) != null) {
            stations.add(section.getDownStation());
        }
        return stations;
    }

    public Section getHeadSection() {
        List<Station> upStations = new ArrayList<>();
        List<Station> downStations = new ArrayList<>();
        for (Section section : sections) {
            upStations.add(section.getUpStation());
            downStations.add(section.getDownStation());
        }
        upStations.removeAll(downStations);
        Station upStationOfHeadSection = upStations.get(0);
        return findByUpStation(upStationOfHeadSection)
                .orElseThrow(() -> {throw new StationNotFoundException(upStationOfHeadSection.getId());});
    }

    private Optional<Section> findByUpStation(Station station) {
        return sections.stream()
                .filter(s -> station.isSameName(s.getUpStation()))
                .findFirst();
    }

    private Optional<Section> findByDownStation(Station station) {
        return sections.stream()
                .filter(s -> station.isSameName(s.getDownStation()))
                .findFirst();
    }

    public Section getNextSection(Section section) {
        Station downStation = section.getDownStation();
        return sections.stream()
                .filter(s -> downStation.isSameName(s.getUpStation()))
                .findFirst()
                .orElse(null);
    }

    private void insertion(Section section) {
        findByUpStation(section.getUpStation())
                .ifPresent(find -> find.shiftBack(section));
        findByDownStation(section.getDownStation())
                .ifPresent(find -> find.shiftForward(section));
    }

    public List<Section> getSections() {
        return sections;
    }
}
