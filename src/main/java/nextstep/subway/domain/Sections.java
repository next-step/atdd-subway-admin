package nextstep.subway.domain;

import nextstep.subway.application.Distance;
import nextstep.subway.application.Distances;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    protected Sections() {

    }

    public List<Section> getSections() {
        return sections;
    }

    public boolean isFirst(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation() == null)
                .anyMatch(section -> section.getDownStation().equals(station));
    }

    public boolean isLast(Station station) {
        return sections.stream()
                .filter(section -> section.getDownStation() == null)
                .anyMatch(section -> section.getUpStation().equals(station));
    }

    public boolean isContainStation(Station station) {
        return getStations().contains(station);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        Station firstStation = sections.stream().filter(section -> section.getUpStation() == null)
                .collect(Collectors.toList()).get(0)
                .getDownStation();
        Station nextStation = firstStation;

        while(nextStation != null) {
            stations.add(nextStation);
            Station finalNextStation = nextStation;
            nextStation = sections.stream().filter(section ->
                            section.getUpStation() != null && section.getUpStation().equals(finalNextStation))
                    .collect(Collectors.toList()).get(0)
                    .getDownStation();
        }
        return stations;
    }

    public List<Section> addAndGetSections(Station requestUpStation, Station requestDownStation, Distance distance) {
        new Distances(sections.stream().map(Section::getDistance).collect(Collectors.toList()))
                .checkLessThanAllSectionDistance(distance);
        Section newSection = new Section(requestUpStation, requestDownStation, distance);
        Station newStation = checkAndGetNewStation(requestUpStation, requestDownStation);

        List<Section> sections = new ArrayList<>();
        if(requestUpStation.equals(newStation)) {
            sections.add(getNewUpStationSection(newStation, requestDownStation, distance));
            sections.add(newSection);
        }
        if (requestDownStation.equals(newStation)) {
            sections.add(newSection);
            sections.add(getNewDownStationSection(requestUpStation, newStation, distance));
        }
        this.sections.addAll(sections);
        return sections;
    }

    private Station checkAndGetNewStation(Station upStation, Station downStation) {
        boolean isContainUpStation = this.isContainStation(upStation);
        boolean isContainDownStation = this.isContainStation(downStation);
        if(isContainUpStation && isContainDownStation) {
            throw new IllegalArgumentException(ErrorMessage.ALREADY_EXIST_SECTION.getMessage());
        }
        if(!isContainUpStation && !isContainDownStation) {
            throw new IllegalArgumentException(ErrorMessage.NO_EXIST_STATIONS.getMessage());
        }
        return isContainUpStation ? downStation : upStation;
    }

    private Section getNewUpStationSection(Station newStation, Station downStation, Distance distance) {
        Section existingSection = sections.stream()
                .filter(section -> downStation.equals(section.getDownStation()))
                .collect(Collectors.toList()).get(0);
        if(isFirst(downStation)) {
            sections.remove(existingSection);
            Section newSection = new Section(null, newStation, new Distance(0, true));
            return newSection;
        }
        sections.remove(existingSection);
        Section newSection = new Section(existingSection.getUpStation(), newStation,
                existingSection.getDistance().getNewSectionDistance(distance));
        return newSection;
    }

    private Section getNewDownStationSection(Station upStation, Station newStation, Distance distance) {
        Section existingSection = sections.stream()
                .filter(section -> upStation.equals(section.getUpStation()))
                .collect(Collectors.toList()).get(0);
        if (isLast(upStation)) {
            sections.remove(existingSection);
            Section newSection = new Section(newStation, null, new Distance(0, true));
            return newSection;
        }
        sections.remove(existingSection);
        Section newSection = new Section(newStation, existingSection.getDownStation(),
                existingSection.getDistance().getNewSectionDistance(distance));
        return newSection;
    }

    public List<Section> init(Section section) {
        sections.add(section);
        sections.add(new Section(null, section.getUpStation(), new Distance(0, true)));
        sections.add(new Section(section.getDownStation(), null, new Distance(0, true)));
        return sections;
    }

}
