package nextstep.subway.section.domain;

import nextstep.subway.line.exception.CanNotDeleteSectionException;
import nextstep.subway.section.exception.CanNotConnectSectionException;
import nextstep.subway.station.domain.Station;
import javax.persistence.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Embeddable
public class Sections {

    @OneToMany(cascade =  CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "lineId")
    private List<Section> sections = new ArrayList<>();

    public Sections(List<Section> sections) {
        this.sections = requireNonNull(sections, "sections");
    }

    public Sections() {
    }

    public void add(Section other) {
        requireNonNull(other, "section");
        if (!sections.isEmpty()) {
            Section section = getConnectableSection(other);
            section.relocate(other);
        }
        sections.add(other);
    }

    private Section getConnectableSection(Section section) {
        return findOneByFilter(s -> s.isConnectable(section))
                .orElseThrow(CanNotConnectSectionException::new);
    }

    public List<Station> getStations() {
        return getSectionsInOrder();
    }

    private List<Station> getSectionsInOrder() {
        Map<Station, Station> map = sections.stream()
                                            .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
        Station station = findUpStationOfHeadSection();
        List<Station> stations = new ArrayList<>();
        while (map.get(station) != null) {
            stations.add(station);
            station = map.get(station);
        }
        stations.add(station);
        return stations;
    }

    private Station findUpStationOfHeadSection() {
        Set<Station> upStations = sections.stream()
                                          .map(Section::getUpStation)
                                          .collect(Collectors.toSet());
        Set<Station> downStations = sections.stream()
                                            .map(Section::getDownStation)
                                            .collect(Collectors.toSet());
        return upStations.stream()
                         .filter(upStation -> !downStations.contains(upStation))
                         .findFirst()
                         .orElseThrow(IllegalArgumentException::new);
    }

    public void remove(Station station) {
        requireNonNull(station, "station가 비었습니다.");
        if (sections.size() <= 1) {
            throw new CanNotDeleteSectionException("더 이상 구간을 제거할 수 없습니다.");
        }
        Optional<Section> sectionIncludingUpStation = findOneByFilter(section -> section.matchesUpStation(station));
        Optional<Section> sectionIncludingDownStation = findOneByFilter(section -> section.matchesDownStation(station));
        removeOrMerge(sectionIncludingUpStation, sectionIncludingDownStation);
    }

    private Optional<Section> findOneByFilter(Predicate<Section> filter) {
        return sections.stream()
                       .filter(filter)
                       .findFirst();
    }

    private void removeOrMerge(Optional<Section> sectionIncludingUpStation,
                               Optional<Section> sectionIncludingDownStation) {
        if (!sectionIncludingUpStation.isPresent() && !sectionIncludingDownStation.isPresent()) {
            throw new CanNotDeleteSectionException("해당 역은 구간에 존재하지 않습니다.");
        }
        if (sectionIncludingUpStation.isPresent() && sectionIncludingDownStation.isPresent()) {
            merge(sectionIncludingUpStation.get(), sectionIncludingDownStation.get());
            return;
        }
        removeOnly(sectionIncludingUpStation, sectionIncludingDownStation);
    }

    private void merge(Section sectionIncludingUpStation, Section sectionIncludingDownStation) {
        sectionIncludingDownStation.merge(sectionIncludingUpStation);
        sections.remove(sectionIncludingUpStation);
    }

    private void removeOnly(Optional<Section> sectionIncludingUpStation,
                            Optional<Section> sectionIncludingDownStation) {
        if (sectionIncludingUpStation.isPresent()) {
            sections.remove(sectionIncludingUpStation.get());
            return;
        }
        if (sectionIncludingDownStation.isPresent()) {
            sections.remove(sectionIncludingDownStation.get());
        }
    }
}
