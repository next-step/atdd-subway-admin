package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
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

    public List<Section> addAndGetSections(Section newSection, Station newStation, Station requestUpStation, Station requestDownStation) {
        List<Section> sections = new ArrayList<>();
        if(requestUpStation.equals(newStation)) {
            sections.add(addNewUpStationAndGetSection(newStation, requestDownStation, newSection.getDistance()));
            sections.add(newSection);
        }
        if (requestDownStation.equals(newStation)) {
            sections.add(newSection);
            sections.add(addNewDownStationAndGetSection(requestUpStation, newStation, newSection.getDistance()));
        }
        this.sections.addAll(sections);
        return sections;
    }

    private Section addNewUpStationAndGetSection(Station newStation, Station downStation, int distance) {
        Section existingSection = sections.stream()
                .filter(section -> downStation.equals(section.getDownStation()))
                .collect(Collectors.toList()).get(0);
        sections.remove(existingSection);
        if(isFirst(downStation)) {
            Section newSection = new Section(null, newStation, 0);
            sections.add(newSection);
            return newSection;
        }
        Section newSection = new Section(existingSection.getUpStation(), newStation,
                existingSection.getDistance() - distance);
        return newSection;
    }

    private Section addNewDownStationAndGetSection(Station upStation, Station newStation, int distance) {
        Section existingSection = sections.stream()
                .filter(section -> upStation.equals(section.getUpStation()))
                .collect(Collectors.toList()).get(0);
        sections.remove(existingSection);
        if (isLast(upStation)) {
            Section newSection = new Section(newStation, null, 0);
            sections.add(newSection);
            return newSection;
        }
        Section newSection = new Section(newStation, existingSection.getDownStation(),
                existingSection.getDistance() - distance);
        return newSection;
    }

    public List<Section> init(Section section) {
        sections.add(section);
        sections.add(new Section(null, section.getUpStation(), 0));
        sections.add(new Section(section.getDownStation(), null, 0));
        return sections;
    }

    public int compareToAllDistance(int distance) {
        return Integer.compare(sections.stream().mapToInt(Section::getDistance).sum(), distance);
    }
}
