package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        sections.add(section);
    }

    public Set<Station> getStations() {
        Set<Station> upStationSet = sections.stream().map(Section::getUpStation).collect(Collectors.toSet());
        Set<Station> downStationSet = sections.stream().map(Section::getDownStation).collect(Collectors.toSet());
        upStationSet.addAll(downStationSet);
        return upStationSet;
    }

}
