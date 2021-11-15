package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {}

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        Set<Station> result = new LinkedHashSet<>();
        sections.forEach(section -> {
            result.add(section.getUpStation());
            result.add(section.getDownStation());
        });
        return new ArrayList<>(result);
    }

}
