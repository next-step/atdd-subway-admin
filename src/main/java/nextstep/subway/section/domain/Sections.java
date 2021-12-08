package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.EAGER)
    private final List<Section> sections = new ArrayList<>();

    @Transient
    private final Stations stations;
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

    public boolean onlyLastSectionRemains() {
        return sections.size() == 1;
    }

    public void addSection(int index, Section section) {
        this.sections.add(index, section);
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public Sections getSortedSections() {
        findFirstLastStation();
        Sections sortedSections = new Sections();
        Section firstSection = sections.stream()
            .filter(section -> section.isUpStation(firstStation))
            .findFirst().get();

        addSectionAndFindNext(firstSection, sortedSections);
        return sortedSections;
    }

    private void addSectionAndFindNext(Section section, Sections sortedSections) {
        sortedSections.addSection(section);
        if (section.isDownStation(lastStation)) {
            return;
        }
        Section nextSection = this.sections.stream()
            .filter(s -> s.isUpStation(section.getDownStation()))
            .findFirst()
            .get();
        addSectionAndFindNext(nextSection, sortedSections);
    }

    public List<Station> getSortedStations() {
        findFirstLastStation();
        addStationAndFindNext(firstStation);
        return stations.getStations();
    }

    private void addStationAndFindNext(Station station) {
        this.stations.addStation(station);

        Station nextStation = this.sections.stream()
            .filter(section -> section.isUpStation(station))
            .map(Section::getDownStation)
            .findFirst()
            .get();

        if (nextStation.isSameStation(lastStation)) {
            this.stations.addStation(nextStation);
            return;
        }
        addStationAndFindNext(nextStation);
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

    public Optional<Section> getUpSectionOf(Station station) {
        return sections.stream()
            .filter(section -> section.isDownStation(station))
            .findFirst();
    }

    public Optional<Section> getDownSectionOf(Station station) {
        return sections.stream()
            .filter(section -> section.isUpStation(station))
            .findFirst();
    }

    public void removeSection(Section section) {
        sections.remove(section);
    }

    public void mergeSections(Section upSection, Section downSection) {
        addSection(sections.indexOf(upSection), getMergedSection(upSection, downSection));
        removeSection(upSection);
        removeSection(downSection);
    }

    public Section getMergedSection(Section upSection, Section downSection) {
        Section mergedSection = new Section();
        mergedSection.setLine(upSection.getLine());
        mergedSection.setUpStation(upSection.getUpStation());
        mergedSection.setDownStation(downSection.getDownStation());
        mergedSection.setDistance(downSection.getDistance() + upSection.getDistance());
        return mergedSection;
    }
}
