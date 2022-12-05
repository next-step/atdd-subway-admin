package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(Section section) {
        sections = Arrays.asList(section);
    }

    /*public Sections(List<Section> sections) {
        this.sections = sections;
    }*/

    public void add(Section section) {
        checkStation(section);
        sections.add(section);
    }

    private void checkStation(Section newSection) {
        sections.stream()
                .filter(section -> section.isCheckStation(newSection))
                .findFirst()
                .ifPresent(section -> section.changeStation(newSection));
    }

    public List<Station> getStations() {
        /*List<Station> stations = new ArrayList<>();
        stations.add(sections.get(0).getUpStation());
        for(Section section : sections) {
            stations.add(section.getDownStation());
        }
        return stations;*/
        return sections.stream()
                .sorted()
                .flatMap(Section::getStations)
                .distinct()
                .collect(Collectors.toList());
    }
}
