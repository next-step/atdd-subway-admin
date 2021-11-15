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

    @OneToMany(mappedBy = "line" ,cascade = {CascadeType.PERSIST, CascadeType.MERGE},orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStation(){
        Set<Station> stationBasket = new LinkedHashSet<>();
        this.sections.stream()
                .forEach(section -> {
                    stationBasket.add(section.getUpStation());
                    stationBasket.add(section.getDownStation());
                });
        return new ArrayList<>(stationBasket);
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }
}
