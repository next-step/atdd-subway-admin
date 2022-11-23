package nextstep.subway.domain;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line")
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

    public void add(Section section) {
        this.sections.add(section);
    }
}
