package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(Section section) {
        sections = Arrays.asList(section);
    }

    public void add(Section section) {
        ifContainsStationAdd(section);
        sections.add(section);
    }

    private void ifContainsStationAdd(Section newSection) {
        sections.stream()
                .filter(section -> section.containsStation(newSection))
                .findFirst()
                .ifPresent(section -> section.swapStation(newSection));
    }



    public List<Station> getStations() {
        return sections.stream()
                .sorted()
                .flatMap(Section::getStations)
                .distinct()
                .collect(Collectors.toList());

    }
}
